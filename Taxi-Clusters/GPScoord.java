public class GPScoord {
    private double longitude;
    private double latitude;

    public GPScoord(double lon, double lat) {
        longitude = lon;
        latitude = lat;
    }

    // Getter for longitude
    public double getLongitude() {
        return longitude;
    }

    // Getter for latitude
    public double getLatitude() {
        return latitude;
    }

    // This function takes a GPScoord names p2 and returns the distance between this
    // instance and p2.
    public double distFunc(GPScoord p2) {
        return Math.sqrt(((longitude - p2.getLongitude()) * (longitude - p2.getLongitude()))
                + ((latitude - p2.getLatitude()) * (latitude - p2.getLatitude())));
    }
}
