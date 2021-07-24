package Rides;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

public class Location {
    double latitude;
    double longitude;
    private Object location;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double distanceTo(Location previousLocation) {
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;
        GlobalPosition pointA = new GlobalPosition(latitude, longitude, 0.0); // Point A
        GlobalPosition pointB = new GlobalPosition(previousLocation.latitude, previousLocation.longitude, 0.0); // Point B
        return geoCalc.calculateGeodeticCurve(reference, pointB, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B in meters
    }

    @Override
    public String toString() {
        return "lat: " + latitude + " lon: " + longitude;
    }

    @Override
    public boolean equals(Object location) {
        return (latitude == ((Location) location).latitude && longitude == ((Location) location).longitude);
    }
}