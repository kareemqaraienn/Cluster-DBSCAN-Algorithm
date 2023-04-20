import java.util.ArrayList;

public class Cluster {
    private ArrayList<GPScoord> cluster;

    public Cluster() {
        cluster = new ArrayList<>();
    }

    // returns size of the cluster
    public int clusterSize() {
        return cluster.size();
    }

    // adds a GPScoord point into the cluster
    public void addPoint(GPScoord point) {
        cluster.add(point);

    }

    // returns the cluster
    public ArrayList<GPScoord> getCluster() {
        return cluster;
    }

    // adds all the points of an ArrayList of GPScoords named points into the
    // cluster
    public void addPoints(ArrayList<GPScoord> points) {
        for (GPScoord point : points) {
            cluster.add(point);
        }
    }

    // returns the average longitude and latitude of all points in cluster
    public double[] getAverage() {
        double[] average = new double[2];
        double sumLong = 0.00;
        double sumLat = 0.00;
        if (this.clusterSize() == 0) {
            return new double[] { sumLong, sumLat };
        }
        for (GPScoord point : cluster) {
            sumLong += point.getLongitude();
            sumLat += point.getLatitude();
        }
        average[0] = sumLong / clusterSize();
        average[1] = sumLat / clusterSize();
        return average;
    }
}
