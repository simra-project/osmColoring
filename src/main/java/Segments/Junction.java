package Segments;

import geobroker.Geofence;
import geobroker.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static Config.Config.*;

public class Junction extends Segment implements Comparable<Junction> {
    public int numberOfRides, numberOfIncidents, numberOfScaryIncidents, numberOfNonScaryIncidents;
    public int[] scaryIncidentTypes = new int[9], nonScaryIncidentTypes = new int[9], lanes_bw;
    public double dangerousnessScore;


    public Junction(String id, double[] lats, double[] lons, HashSet<String> highwayName, String[] highWayTypes, int[] highWayLanes, int[] lanes_bw, double[] polyLats, double[] polyLons) {
        this.id = id;
        this.lats = lats;
        this.lons = lons;
        this.highwayName = highwayName;
        this.highWayTypes = highWayTypes;
        this.highWayLanes = highWayLanes;
        this.lanes_bw = lanes_bw;
        this.poly_vertices_latsArray = polyLats;
        this.poly_vertices_lonsArray = polyLons;
        // geofence needs each location once. So omit the last gps point, which is double because of first and last point.
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < polyLats.length-1; i++) {
            locations.add(new Location(polyLats[i],polyLons[i]));
        }
        this.geofence = Geofence.Companion.polygon(locations);
    }

    public Double getScore() {
        if (String.valueOf(this.dangerousnessScore).equals("NaN")) {
            return -1.0;
        } else {
            return this.dangerousnessScore;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("JUNCTION:");
        result.append("|score:").append(dangerousnessScore);
        result.append("|id:").append(id);
        result.append("|coordinates:[");
        for (int i = 0; i < lats.length; i++) {
            result.append(lats[i]).append(",").append(lons[i]).append(" ");
        }
        result.append(lats[lats.length-1]).append(",").append((lons[lons.length-1])).append("]");
        result.append(super.toString());
        return result.toString();
    }


    @Override
    public int compareTo(@NotNull Junction o) {
        return this.getScore().compareTo(o.getScore());
    }

    public String toGeoJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"Feature\",\"id\":\"").append(id)
                .append("\",\"properties\":{\"type\":\"Street\",")
                .append("\n\"score\":").append(getScore())
                .append(",\n\"incidents\":").append((numberOfNonScaryIncidents + numberOfScaryIncidents))
                .append(",\n\"rides\":").append(numberOfRides)
                .append(",\n\"clopa\":").append((nonScaryIncidentTypes[1] + scaryIncidentTypes[1]))
                .append(",\n\"spiot\":").append((nonScaryIncidentTypes[2] + scaryIncidentTypes[2]))
                .append(",\n\"nlorh\":").append((nonScaryIncidentTypes[3] + scaryIncidentTypes[3]))
                .append(",\n\"ssho\":").append((nonScaryIncidentTypes[4] + scaryIncidentTypes[4]))
                .append(",\n\"tailgating\":").append((nonScaryIncidentTypes[5] + scaryIncidentTypes[5]))
                .append(",\n\"near-dooring\":").append((nonScaryIncidentTypes[6] + scaryIncidentTypes[6]))
                .append(",\n\"dao\":").append((nonScaryIncidentTypes[7] + scaryIncidentTypes[7]))
                .append(",\n\"other\":").append((nonScaryIncidentTypes[8] + scaryIncidentTypes[8]))
                .append(super.toGeoJson())
                .append("},\n\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[");

        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            result.append("[").append(poly_vertices_lonsArray[i]).append(",").append(poly_vertices_latsArray[i]).append("],");
        }
        result.append("[").append(poly_vertices_lonsArray[poly_vertices_lonsArray.length-1]).append(",").append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append("]]]}}");



        return result.toString();
    }
}
