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

    public String determineColor(double score) {
        String color = "#1a9641";
        double opacity = 0.1;
        int weight = 1;
        if (score >= 0.5) {
            color = "#d7191c";
            opacity = 0.7;
            weight = 5;
        } else if (score >= 0.25) {
            color = "#ff6600";
            opacity = 0.7;
            weight = 5;
        } else if (score > 0.05) {
            color = "#ffff00";
            opacity = 0.7;
            weight = 5;
        }
        return color+","+opacity + "," + weight;
    }
}
