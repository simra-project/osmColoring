package Segments;

import de.hasenburg.geobroker.commons.model.spatial.Geofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Segment {
    // junctions and streets
    public String id;
    public String[] highWayTypes;
    public HashSet<String> highwayName;
    public int[] highWayLanes;
    public Geofence geofence;
    public double[] lats, lons;
    public List<Segment> neighbors = new ArrayList<>();
    public double[] poly_vertices_latsArray, poly_vertices_lonsArray;

    public void addNeighbor(Segment newNeighbor) {
        this.neighbors.add(newNeighbor);
    }


    public double calculateDistanceWithHaversine() {
        double R = 6372800; // In meters
        double length = 0.0;
        for (int i = 1; i < lats.length; i++) {
            double dLat = Math.toRadians(lats[i] - lats[i-1]);
            double dLon = Math.toRadians(lons[i] - lons[i-1]);
            double lat1 = Math.toRadians(lats[i-1]);
            double lat2 = Math.toRadians(lats[i]);

            double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            length += R * c;
        }

        return length;
    }

    public Segment() {

    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("|names:").append(highwayName)
                .append("|types:").append(Arrays.toString(highWayTypes));

        result.append("|poly_coordinates:[");
        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            result.append(poly_vertices_latsArray[i]).append(",").append(poly_vertices_lonsArray[i]).append(" ");
        }
        result.append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append(",").append((poly_vertices_lonsArray[poly_vertices_lonsArray.length-1])).append("]");
        return result.toString();
    }
}
