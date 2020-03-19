package Graph;

import de.hasenburg.geobroker.commons.model.spatial.Geofence;
import de.hasenburg.geobroker.commons.model.spatial.Location;
import jdk.javadoc.doclet.Taglet;

import javax.tools.JavaFileManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Segment {
    // junctions and streets
    public String id;
    public String[] highwayName, highWayTypes;
    public int[] highWayLanes;
    public Geofence geofence;
    public double[] lats, lons;
    public boolean isJunction;
    public List<Segment> neighbors = new ArrayList<>();
    public double[] poly_vertices_latsArray, poly_vertices_lonsArray;

    // only streets
    public int lanes, lanes_backward, numberOfSegmentOfStreet, length;
    public String partOfStreetWithId;
    public String[] segment_nodes;
    public int numberOfRidesSouthWest, numberOfRidesNorthEast, numberOfIncidentsSouthWest, numberOfIncidentsNortheast;
    public int[] scaryIncidentTypesSouthWest, scaryIncidentTypesNorthEast, nonScaryIncidentTypesSouthWest, nonScaryIncidentTypesNorthEast;
    public double scoreSouthWest, scoreNorthEast;

    // only junctions
    public int numberOfRides, numberOfIncidents;
    public int[] scaryIncidentTypes, nonScaryIncidentTypes;
    public double dangerousnessScore;

    // constructor for a junction
    public Segment(String id, double[] lats, double[] lons, String[] highwayName, String[] highWayTypes, int[] highWayLanes, double[] polyLats, double[] polyLons) {
        this.isJunction = true;
        this.id = id;
        this.lats = lats;
        this.lons = lons;
        this.highwayName = highwayName;
        this.highWayTypes = highWayTypes;
        this.highWayLanes = highWayLanes;
        this.poly_vertices_latsArray = polyLats;
        this.poly_vertices_lonsArray = polyLons;
        // geofence needs each location once. So omit the last gps point, which is double because of first and last point.
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < polyLats.length-1; i++) {
            locations.add(new Location(polyLats[i],polyLons[i]));
        }
        this.geofence = Geofence.Companion.polygon(locations);
        // System.out.println("geofence: " + geofence.toString());
    }

    // Constructor for a street segment
    public Segment(String id, String segment_nr, double[] lats, double[] lons, String highWayName, String[] highWayTypes, int highwayLanes, int highwayLanes_backward, String[] segment_nodes, double[] poly_vertices_latsArray, double[] poly_vertices_lonsArray) {
        this.isJunction = false;
        this.highWayTypes = highWayTypes;
        this.id = id;
        this.highwayName = new String[] {highWayName};
        this.lanes = highwayLanes;
        this.lanes_backward = highwayLanes_backward;
        this.segment_nodes = segment_nodes;
        this.partOfStreetWithId = segment_nr.split("\\.")[0];
        this.numberOfSegmentOfStreet = Integer.valueOf(segment_nr.split("\\.")[1]);
        this.lats = lats;
        this.lons = lons;
        this.poly_vertices_latsArray = poly_vertices_latsArray;
        this.poly_vertices_lonsArray = poly_vertices_lonsArray;
        // geofence needs each location once. So omit the last gps point, which is double because of first and last point.
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            locations.add(new Location(poly_vertices_latsArray[i],poly_vertices_lonsArray[i]));
        }
        this.geofence = Geofence.Companion.polygon(locations);
        // System.out.println("geofence: " + geofence.toString());
    }

    public void addNeighbor(Segment newNeighbor) {
        this.neighbors.add(newNeighbor);
    }

    @SuppressWarnings("Duplicates")
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (isJunction) {
            result.append("JUNCTION:");
        } else {
            result.append("STREET:");
        }
        result.append("|id:").append(id);
        /*
        result.append("|neighbors:[");
        System.out.println("neighbors.size(): " + neighbors.size());
        if (neighbors.size() > 0) {
        for (int i = 0; i < neighbors.size()-1; i++) {
            result.append(neighbors.get(i).id).append(",");
        }
            result.append(neighbors.get(neighbors.size()-1)).append("]");
        }
        */
        result.append("|names:").append(Arrays.toString(highwayName))
                .append("|types:").append(Arrays.toString(highWayTypes));
        result.append("|coordinates:[");
        for (int i = 0; i < neighbors.size()-1; i++) {
            result.append(lats[i]).append(",").append(lons[i]).append(" ");
        }
        result.append(lats[lats.length-1]).append(",").append((lons[lons.length-1])).append("]");
        result.append("|poly_coordinates:[");
        for (int i = 0; i < neighbors.size()-1; i++) {
            result.append(poly_vertices_latsArray[i]).append(",").append(poly_vertices_lonsArray[i]).append(" ");
        }
        result.append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append(",").append((poly_vertices_lonsArray[poly_vertices_lonsArray.length-1])).append("]");

        return result.toString();
    }

    public boolean getIsJunction() {
        return isJunction;
    }

    private double calculateLength() {
        double length = 0.0;
        for (int i = 1; i < lats.length; i++) {
            double latLength = (lats[i] - lats[i-1]) * Math.PI / 180;
            double lonLength = (lons[i] - lons[i-1]) * Math.PI / 180;
            double a = Math.pow(Math.sin(latLength / 2),2)
                    + (Math.cos(lats[i-1])*Math.PI/180) * (Math.cos(lats[i])*Math.PI/180)
                    * Math.pow(Math.sin(lonLength / 2),2);
            length += 6371000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        }
        return length;
    }

}
