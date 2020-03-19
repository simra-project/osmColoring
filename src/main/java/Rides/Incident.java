package Rides;

public class Incident {
    double lat, lon;
    long timestamp;
    boolean child, trailer, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, scary;
    int pLoc, incident, bike;
    String description;

    public Incident( double lat, double lon, long timestamp, int bike, boolean child, boolean trailer, int pLoc, int incident, boolean i1, boolean i2, boolean i3, boolean i4, boolean i5, boolean i6, boolean i7, boolean i8, boolean i9, boolean scary, String description, boolean i10) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.bike = bike;
        this.child = child;
        this.trailer = trailer;
        this.pLoc = pLoc;
        this.incident = incident;
        this.i1 = i1;
        this.i2 = i2;
        this.i3 = i3;
        this.i4 = i4;
        this.i5 = i5;
        this.i6 = i6;
        this.i7 = i7;
        this.i8 = i8;
        this.i9 = i9;
        this.scary = scary;
        this.description = description;
        this.i10 = i10;
    }

}
