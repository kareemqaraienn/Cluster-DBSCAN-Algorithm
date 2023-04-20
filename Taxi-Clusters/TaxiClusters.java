import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;

//Student Name: Kareem Qaraien
//Student Number: 300200598
public class TaxiClusters {

    public static void main(String[] args) {

        TaxiClusters tc = new TaxiClusters();
        // converts all the rows of trips into an arraylist of GPScoords
        ArrayList<GPScoord> allPoints = tc.readAndPopulate("data.csv");
        // check if file is empty to stop program
        if (allPoints.size() == 0) {
            System.out.println("File is empty");
            return;
        }
        // groups allPoints into clusters based on the eps and minPts
        ArrayList<Cluster> clusters = tc.DBSCAN(allPoints, 0.0001, 5);
        if (clusters.size() == 0) { // If there arent any clusters stop program
            System.out.println("No clusters were obtained");
            return;
        }
        try {
            // prints out all the clusters into csv format
            tc.printOutputCSV(clusters);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected ArrayList<GPScoord> readAndPopulate(String fileName) {
        ArrayList<GPScoord> allPoints = new ArrayList<>();
        String trip = "";
        boolean flag = true;
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(fileName));
            while ((trip = br.readLine()) != null) {
                // excludes first row because of column names
                if (flag) {
                    flag = false;
                    continue;
                } else {
                    // create a TripRecord of each row in the csv file
                    String[] tripRec = trip.split(",");
                    // create a GPScoord point of the tripRec
                    GPScoord point = new GPScoord(Double.parseDouble(tripRec[8]), Double.parseDouble(tripRec[9]));
                    allPoints.add(point);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.exit(0);

        }
        return allPoints;

    }

    protected ArrayList<GPScoord> RangeQuery(ArrayList<GPScoord> allpoints, GPScoord actualPoint, double eps) {
        ArrayList<GPScoord> neighbours = new ArrayList<>();
        for (GPScoord point : allpoints) {
            if (point != actualPoint && actualPoint.distFunc(point) <= eps) {
                neighbours.add(point);
            }

        }
        return neighbours;
    }

    protected ArrayList<Cluster> DBSCAN(ArrayList<GPScoord> allPoints, double eps, int minPts) {

        Cluster cluster;
        ArrayList<GPScoord> visited = new ArrayList<>();
        ArrayList<Cluster> clusters = new ArrayList<>();
        ArrayList<GPScoord> noise = new ArrayList<>();
        ArrayList<GPScoord> neighbours;
        for (GPScoord point : allPoints) {
            cluster = new Cluster();
            // if was previously accounted for
            if (visited.contains(point) || noise.contains(point)) {
                continue;
            }
            // get neighbours of point
            neighbours = RangeQuery(allPoints, point, eps);
            // density check
            if (neighbours.size() < minPts) {
                noise.add(point);
                continue;
            }
            visited.add(point);
            cluster.addPoint(point);
            // neighbours to expand
            ArrayList<GPScoord> seedSet = neighbours;
            for (int i = 0; i < seedSet.size(); i++) {
                GPScoord point2 = seedSet.get(i);
                if (noise.contains(point2)) {
                    // taken from noise into cluster
                    noise.remove(point2);
                    visited.add(point2);
                    cluster.addPoint(point2);
                }
                // if it was visited already
                if (noise.contains(point2) || visited.contains(point2)) {
                    continue;
                }
                visited.add(point2);
                cluster.addPoint(point2);
                ArrayList<GPScoord> expandedNeighbours = RangeQuery(allPoints, point2, eps);
                if (expandedNeighbours.size() >= minPts) {
                    // eligible to be in cluster
                    for (int x = 0; x < expandedNeighbours.size(); x++) {
                        GPScoord neighbour = expandedNeighbours.get(x);
                        // check if point is already in seedset
                        if (!seedSet.contains(neighbour)) {
                            seedSet.add(neighbour);
                        }
                    }
                }

            }
            clusters.add(cluster);

        }
        return clusters;
    }

    protected void printOutputCSV(ArrayList<Cluster> clusters) throws IOException {
        try {
            File csvFile = new File("output.csv");
            FileWriter fileWriter = new FileWriter(csvFile);
            int index = 1;
            for (Cluster cluster : clusters) {
                StringBuilder line = new StringBuilder();
                // print column names
                if (index == 1) {
                    line.append("Cluster ID,Longitude,Latitude,Number of points\n");
                }
                // print out the values
                line.append(index + " , " + cluster.getAverage()[0] + " , "
                        + cluster.getAverage()[1] + " , " + cluster.clusterSize());
                index++;
                line.append("\n");
                fileWriter.write(line.toString());
            }
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("output file cannot be found");
            System.exit(0);
        }
    }
}
