public class TripRecord {
    private String pickup_DateTime;
    private GPScoord pickup_Location;
    private GPScoord dropoff_Location;
    private double trip_Distance;

    public TripRecord(String pickup_DateTime, GPScoord pickup_Location, GPScoord dropoff_Location,
            double trip_Distance) {
        this.pickup_DateTime = pickup_DateTime;
        this.pickup_Location = pickup_Location;
        this.dropoff_Location = dropoff_Location;
        this.trip_Distance = trip_Distance;

    }

    // All the methods below are getters for each attribute
    public String getPickup_DateTime() {
        return this.pickup_DateTime;
    }

    public GPScoord getPickup_Location() {
        return this.pickup_Location;
    }

    public GPScoord getDropff_Location() {
        return this.dropoff_Location;
    }

    public double getTrip_Distance() {
        return this.trip_Distance;
    }

}