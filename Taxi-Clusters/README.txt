Student Name: Kareem Qaraien
Student Number: 300200598


IMPORTANT: Instructions to run:
                                In the TaxiClusters class, line 14(main); add the name of the csv file you would like to obtain the values from.
                                On line 15, the second and third parameters of DBSCAN are eps and minPts respectively. Change them to the
                                preference of what values it would be tested on. After running the program, the output should be in a file called "output.csv"
                                which is included in the file of this assignment. If user has a different preference for the name; on line 110, change "output.csv"
                                to the user's preference output file name.
Classes summary:
        GPScoord:
                 This class has 2 private attributes; longitude and latitude. 
                 It has it's getters for both attributes and a function that calculates the distance.
        TripRecord:
                 This class has 4 private attributes; pickup_DateTime, pickup_Location, dropoff_Location, and trip_Distance.
                 It has it's getters for all attributes. This class was only used to translate each taxi trip into a TripRecord to
                 obtain the GPScoords from it later.
        Cluster:
                 This class has 1 attribute; cluster, which is an array list of GPScoords.
                 it contains many methods such as clusterSize(), getAverage() and many more.
                 This class was mainly used in TaxiCluster where a database of GPScoords where grouped
                 into clusters.
        TaxiCluster:
                 This is the class where it uses all 3 of the above classes. It reads the csv file and creates a TripRecord of each trip.
                 Then, creates a GPScoord for each tripRecord and populates an empty arraylist of GPScoords into all the trips. (this happens in readAndPopulate method).
                 After that, the user gives 2 inputs, one for the eps and one for minPts. The method DBSCAN takes those 2 inputs and the arrayList of GPScoords
                 that was done in readAndPopulate as a parameter. The purpose of DBSCAN is to use an algorithm that groups the GPScoords into clusters based on
                 the user inputs for eps and minPts. DBSCAN returns an arraylist of clusters which is used in the printOutputCSV. This method takes an arraylist of clusters
                 and prints them into a csv file.
References:
        TaxiCluster:
                The DBSCAN algo: https://en.wikipedia.org/wiki/DBSCAN 
                printOutputCSV (using fileWriter) : https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file
        

