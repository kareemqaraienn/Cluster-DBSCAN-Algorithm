// Project CSI2120/CSI2520
// Winter 2022
// Robert Laganiere, uottawa.ca

package main

import (
	"encoding/csv"
	"fmt"
	"io"
	"math"
	"os"
	"runtime"
	"strconv"
	"sync"
	"time"
)

type Job struct {
	partition []LabelledGPScoord
	offset    int
}
type GPScoord struct {
	lat  float64
	long float64
}

type LabelledGPScoord struct {
	GPScoord
	ID    int // point ID
	Label int // cluster ID
}

const Threads int = 50
const N int = 20
const MinPts int = 5
const eps float64 = 0.0003
const filename string = "yellow_tripdata_2009-01-15_9h_21h_clean.csv"

func main() {

	start := time.Now()

	gps, minPt, maxPt := readCSVFile(filename)
	fmt.Printf("Number of points: %d\n", len(gps))

	minPt = GPScoord{40.7, -74.}
	maxPt = GPScoord{40.8, -73.93}

	// geographical limits
	fmt.Printf("SW:(%f , %f)\n", minPt.lat, minPt.long)
	fmt.Printf("NE:(%f , %f) \n\n", maxPt.lat, maxPt.long)

	// Parallel DBSCAN STEP 1.
	incx := (maxPt.long - minPt.long) / float64(N)
	incy := (maxPt.lat - minPt.lat) / float64(N)

	var grid [N][N][]LabelledGPScoord // a grid of GPScoord slices

	// Create the partition
	// triple loop! not very efficient, but easier to understand

	partitionSize := 0
	for j := 0; j < N; j++ {
		for i := 0; i < N; i++ {

			for _, pt := range gps {

				// is it inside the expanded grid cell
				if (pt.long >= minPt.long+float64(i)*incx-eps) && (pt.long < minPt.long+float64(i+1)*incx+eps) && (pt.lat >= minPt.lat+float64(j)*incy-eps) && (pt.lat < minPt.lat+float64(j+1)*incy+eps) {

					grid[i][j] = append(grid[i][j], pt) // add the point to this slide
					partitionSize++
				}
			}
		}
	}

	//producer thread that produces jobs (partition to be clustered)

	jobs := make(chan Job, N*N/Threads)
	var mutex sync.WaitGroup
	mutex.Add(Threads)

	for j := 0; j < Threads; j++ {
		go consumer(jobs, &mutex)
	}
	for x := 0; x < N; x++ {
		for y := 0; y < N; y++ {
			jobs <- Job{grid[x][y], 10000000*x + 1000000*y}
		}
	}
	close(jobs)
	mutex.Wait()
	end := time.Now()
	fmt.Printf("\nExecution time: %s of %d points\n", end.Sub(start), partitionSize)
	fmt.Printf("Number of CPUs: %d", runtime.NumCPU())
}

// Applies DBSCAN algorithm on LabelledGPScoord points
// LabelledGPScoord: the slice of LabelledGPScoord points
// MinPts, eps: parameters for the DBSCAN algorithm
// offset: label of first cluster (also used to identify the cluster)
// returns number of clusters found

func DBscan(coords []LabelledGPScoord, offset int) (C int) {
	C = 0
	SeedSet := make([]*LabelledGPScoord, 0) //create SeedSet array

	for _, pt := range coords { //loop through each point in the partition
		if pt.Label != 0 { //check if the point was accounted for previously
			continue
		}
		Neighbours := RangeQuery(coords, pt)
		if len(Neighbours) < MinPts { //if the neighbouts of the point dont qualify for the condition of being greater than minPts then label the point as noise
			pt.Label = -1
			continue
		}
		C++

		pt.Label = C + offset //label point
		SeedSet = Neighbours

		for i := 0; i < len(SeedSet); i++ { //loop through the elements of seedset
			point := SeedSet[i]    //set point to be a temp var of the seedset we are at
			if point.Label == -1 { //check if point is noise
				point.Label = C + offset //label point
			}
			if point.Label != 0 { //check if point was checked before
				continue
			}
			point.Label = C + offset                         // label point
			ExpandedNeighbours := RangeQuery(coords, *point) //find expanded neigbours of point
			if len(ExpandedNeighbours) >= MinPts {           //if expandedneighbours qualify the condition of being greater than minPts then add them to SeedSet
				for k := 0; k < len(ExpandedNeighbours); k++ {
					SeedSet = append(SeedSet, ExpandedNeighbours[k])
				}

			}
		}

	}

	// End of DBscan function
	// Printing the result (do not remove)
	fmt.Printf("Partition %10d : [%4d,%6d]\n", offset, C, len(coords))
	return C
}

//this function finds the neighbours of a specific point based on the eps and distance (if eligible) and returns them using pointers
func RangeQuery(coords []LabelledGPScoord, point LabelledGPScoord) []*LabelledGPScoord {
	Neighbours := make([]*LabelledGPScoord, 0)
	for i := 0; i < len(coords); i++ {
		if coords[i].ID != point.ID && distFun(coords[i], point) <= eps {
			Neighbours = append(Neighbours, &coords[i])
		}
	}

	return Neighbours

}

//this function calculates the distance between 2 points
func distFun(p1 LabelledGPScoord, p2 LabelledGPScoord) (distance float64) {
	return math.Sqrt((p2.GPScoord.lat-p1.GPScoord.lat)*(p2.GPScoord.lat-p1.GPScoord.lat) + (p2.GPScoord.long-p1.GPScoord.long)*(p2.GPScoord.long-p1.GPScoord.long))
}

// consumer function
func consumer(jobs chan Job, done *sync.WaitGroup) {
	for {
		j, more := <-jobs

		if more {
			DBscan(j.partition, j.offset)

		} else {
			done.Done()
			return
		}
	}
}

// reads a csv file of trip records and returns a slice of the LabelledGPScoord of the pickup locations
// and the minimum and maximum GPS coordinates
func readCSVFile(filename string) (coords []LabelledGPScoord, minPt GPScoord, maxPt GPScoord) {

	coords = make([]LabelledGPScoord, 0, 5000)

	// open csv file
	src, err := os.Open(filename)
	defer src.Close()
	if err != nil {
		panic("File not found...")
	}

	// read and skip first line
	r := csv.NewReader(src)
	record, err := r.Read()
	if err != nil {
		panic("Empty file...")
	}

	minPt.long = 1000000.
	minPt.lat = 1000000.
	maxPt.long = -1000000.
	maxPt.lat = -1000000.

	var n int = 0

	for {
		// read line
		record, err = r.Read()

		// end of file?
		if err == io.EOF {
			break
		}

		if err != nil {
			panic("Invalid file format...")
		}

		// get lattitude
		lat, err := strconv.ParseFloat(record[9], 64)
		if err != nil {
			panic("Data format error (lat)...")
		}

		// is corner point?
		if lat > maxPt.lat {
			maxPt.lat = lat
		}
		if lat < minPt.lat {
			minPt.lat = lat
		}

		// get longitude
		long, err := strconv.ParseFloat(record[8], 64)
		if err != nil {
			panic("Data format error (long)...")
		}

		// is corner point?
		if long > maxPt.long {
			maxPt.long = long
		}

		if long < minPt.long {
			minPt.long = long
		}

		// add point to the slice
		n++
		pt := GPScoord{lat, long}
		coords = append(coords, LabelledGPScoord{pt, n, 0})
	}

	return coords, minPt, maxPt
}
