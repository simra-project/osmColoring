package Segments;

import geobroker.Geofence;
import geobroker.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static Config.Config.*;

public class Street extends Segment implements Comparable<Street> {
    public int lanes, numberOfSegmentOfStreet;
    public String partOfStreetWithId;
    public String[] segment_nodes;
    public int numberOfRidesSouthWest, numberOfRidesNorthEast, numberOfIncidentsSouthWest, numberOfIncidentsNorthEast, numberOfScaryIncidentsSouthWest, numberOfScaryIncidentsNorthEast, numberOfNonScaryIncidentsSouthWest, numberOfNonScaryIncidentsNorthEast;
    public int[] lanes_backward, scaryIncidentTypesSouthWest = new int[9], scaryIncidentTypesNorthEast = new int[9], nonScaryIncidentTypesSouthWest = new int[9], nonScaryIncidentTypesNorthEast = new int[9];
    public double seg_length, scoreSouthWest, scoreNorthEast, score;

    public Street(String id, String highWayName, String[] highWayTypes, int highwayLanes, int[] highwayLanes_backward, String[] segment_nodes, double seg_length, double[] poly_vertices_latsArray, double[] poly_vertices_lonsArray) {
        super();
        this.highWayTypes = highWayTypes;
        this.id = id;
        this.highwayName = new HashSet<String>(Collections.singletonList(highWayName));
        this.lanes = highwayLanes;
        this.lanes_backward = highwayLanes_backward;
        this.segment_nodes = segment_nodes;
        //this.partOfStreetWithId = segment_nr.split("\\.")[0];
        //this.numberOfSegmentOfStreet = Integer.valueOf(segment_nr.split("\\.")[1]);
        this.seg_length = seg_length;
        this.poly_vertices_latsArray = poly_vertices_latsArray;
        this.poly_vertices_lonsArray = poly_vertices_lonsArray;
        // geofence needs each location once. So omit the last gps point, which is double because of first and last point.
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            locations.add(new Location(poly_vertices_latsArray[i],poly_vertices_lonsArray[i]));
        }
        this.geofence = Geofence.Companion.polygon(locations);
    }

    public Double getScore() {
        if (!String.valueOf(this.scoreNorthEast).equals("NaN") && !String.valueOf(this.scoreSouthWest).equals("NaN")) {
            return this.scoreSouthWest + this.scoreNorthEast;
        } else if (String.valueOf(this.scoreNorthEast).equals("NaN") && !String.valueOf(this.scoreSouthWest).equals("NaN")) {
            return this.scoreSouthWest;
        } else if (!String.valueOf(this.scoreNorthEast).equals("NaN") && String.valueOf(this.scoreSouthWest).equals("NaN")) {
            return this.scoreNorthEast;
        } else {
            return -1.0;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("STREET:");
        result.append("|score:").append(getScore());
        result.append("|id:").append(id);
        result.append("|southWest score:").append(scoreSouthWest);
        result.append("|northEast score:").append(scoreNorthEast);

        result.append(super.toString());
        return result.toString();
    }


    @Override
    public int compareTo(@NotNull Street o) {
        return this.getScore().compareTo(o.getScore());
    }

    public String toGeoJson() {
        StringBuilder result = new StringBuilder();
        result.append("{\"type\":\"Feature\",\"id\":\"").append(id)
                .append("\",\"properties\":{\"type\":\"Street\",")
                .append("\n\"score\":").append(getScore())
                .append(",\n\"incidents\":").append((numberOfNonScaryIncidentsNorthEast + numberOfNonScaryIncidentsSouthWest + numberOfScaryIncidentsNorthEast + numberOfScaryIncidentsSouthWest))
                .append(",\n\"rides\":").append((numberOfRidesSouthWest + numberOfRidesNorthEast))
                .append(",\n\"length\":").append(seg_length)
                .append(",\n\"rides south west\":").append(numberOfRidesSouthWest)
                .append(",\n\"incidents south west\":").append((numberOfNonScaryIncidentsSouthWest + numberOfScaryIncidentsSouthWest))
                .append(",\n\"score south west\":").append(scoreSouthWest)
                .append(",\n\"rides north east\":").append(numberOfRidesNorthEast)
                .append(",\n\"incidents north east\":").append((numberOfNonScaryIncidentsNorthEast + numberOfScaryIncidentsNorthEast))
                .append(",\n\"score north east\":").append(scoreNorthEast)
                .append(",\n\"clopa\":").append((scaryIncidentTypesSouthWest[1] + nonScaryIncidentTypesSouthWest[1] + scaryIncidentTypesNorthEast[1] + nonScaryIncidentTypesNorthEast[1]))
                .append(",\n\"spiot\":").append((scaryIncidentTypesSouthWest[2] + nonScaryIncidentTypesSouthWest[2] + scaryIncidentTypesNorthEast[2] + nonScaryIncidentTypesNorthEast[2]))
                .append(",\n\"nlorh\":").append((scaryIncidentTypesSouthWest[3] + nonScaryIncidentTypesSouthWest[3] + scaryIncidentTypesNorthEast[3] + nonScaryIncidentTypesNorthEast[3]))
                .append(",\n\"saho\":").append((scaryIncidentTypesSouthWest[4] + nonScaryIncidentTypesSouthWest[4] + scaryIncidentTypesNorthEast[4] + nonScaryIncidentTypesNorthEast[4]))
                .append(",\n\"tailgating\":").append((scaryIncidentTypesSouthWest[5] + nonScaryIncidentTypesSouthWest[5] + scaryIncidentTypesNorthEast[5] + nonScaryIncidentTypesNorthEast[5]))
                .append(",\n\"nd\":").append((scaryIncidentTypesSouthWest[6] + nonScaryIncidentTypesSouthWest[6] + scaryIncidentTypesNorthEast[6] + nonScaryIncidentTypesNorthEast[6]))
                .append(",\n\"dao\":").append((scaryIncidentTypesSouthWest[7] + nonScaryIncidentTypesSouthWest[7] + scaryIncidentTypesNorthEast[7] + nonScaryIncidentTypesNorthEast[7]))
                .append(",\n\"other\":").append((scaryIncidentTypesSouthWest[8] + nonScaryIncidentTypesSouthWest[8] + scaryIncidentTypesNorthEast[8] + nonScaryIncidentTypesNorthEast[8]))
                .append(super.toGeoJson())
                .append("},\n\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[");

        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            result.append("[").append(poly_vertices_lonsArray[i]).append(",").append(poly_vertices_latsArray[i]).append("],");
        }
        result.append("[").append(poly_vertices_lonsArray[poly_vertices_lonsArray.length-1]).append(",").append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append("]]]}}");



        return result.toString();
    }

    public String toLeaflet(boolean debugOnMap) {
        StringBuilder result = new StringBuilder();
        result.append("\t\tL.polygon([\n");
        for (int i = 0; i < poly_vertices_latsArray.length; i++) {
            result.append("\t\t\t[")
                    .append(poly_vertices_latsArray[i]).append(",")
                    .append(poly_vertices_lonsArray[i]).append("],\n");
        }
        String color = IRRELEVANT_COLOR;
        if (numberOfRidesSouthWest + numberOfRidesNorthEast >= RELEVANCE_THRESHOLD_RIDECOUNT) {
            color = determineColor().split(",")[0];
        }
        result.append("\t\t],{fillOpacity:").append(determineColor().split(",")[1])
                .append(",color:'").append(color)
                .append("', weight:")
                .append(determineColor().split(",")[2]).append("}).addTo(map)")
                .append(".bindPopup(\"Street")
                .append("<br>score: ").append(getScore())
                .append("<br>incidents: ").append((numberOfNonScaryIncidentsNorthEast + numberOfNonScaryIncidentsSouthWest + numberOfScaryIncidentsNorthEast + numberOfScaryIncidentsSouthWest))
                .append("<br>rides: ").append((numberOfRidesSouthWest + numberOfRidesNorthEast))
                .append("<br>length: ").append(seg_length);
        if (debugOnMap) {
            result.append("<br>id: ").append(id)
                    .append("<br>highwaynames: ").append(highwayName)
                    .append("<br>highwaytypes: ").append(Arrays.toString(highWayTypes));
        }
        result.append("<br>rides south west: ").append(numberOfRidesSouthWest)
                .append("<br>incidents south west: ").append((numberOfNonScaryIncidentsSouthWest + numberOfScaryIncidentsSouthWest))
                .append("<br>score south west: ").append(scoreSouthWest)
                .append("<br>rides north east: ").append(numberOfRidesNorthEast)
                .append("<br>incidents north east: ").append((numberOfNonScaryIncidentsNorthEast + numberOfScaryIncidentsNorthEast))
                .append("<br>score north east: ").append(scoreNorthEast)
                .append("<br>----------incident types----------")
                .append("<br>close pass: ").append((scaryIncidentTypesSouthWest[1] + nonScaryIncidentTypesSouthWest[1] + scaryIncidentTypesNorthEast[1] + nonScaryIncidentTypesNorthEast[1]))
                .append("<br>someone pulling in or out: ").append((scaryIncidentTypesSouthWest[2] + nonScaryIncidentTypesSouthWest[2] + scaryIncidentTypesNorthEast[2] + nonScaryIncidentTypesNorthEast[2]))
                .append("<br>near left or right hook: ").append((scaryIncidentTypesSouthWest[3] + nonScaryIncidentTypesSouthWest[3] + scaryIncidentTypesNorthEast[3] + nonScaryIncidentTypesNorthEast[3]))
                .append("<br>someone approaching head on: ").append((scaryIncidentTypesSouthWest[4] + nonScaryIncidentTypesSouthWest[4] + scaryIncidentTypesNorthEast[4] + nonScaryIncidentTypesNorthEast[4]))
                .append("<br>tailgating: ").append((scaryIncidentTypesSouthWest[5] + nonScaryIncidentTypesSouthWest[5] + scaryIncidentTypesNorthEast[5] + nonScaryIncidentTypesNorthEast[5]))
                .append("<br>near-dooring: ").append((scaryIncidentTypesSouthWest[6] + nonScaryIncidentTypesSouthWest[6] + scaryIncidentTypesNorthEast[6] + nonScaryIncidentTypesNorthEast[6]))
                .append("<br>dodging an obstacle: ").append((scaryIncidentTypesSouthWest[7] + nonScaryIncidentTypesSouthWest[7] + scaryIncidentTypesNorthEast[7] + nonScaryIncidentTypesNorthEast[7]))
                .append("<br>other: ").append((scaryIncidentTypesSouthWest[8] + nonScaryIncidentTypesSouthWest[8] + scaryIncidentTypesNorthEast[8] + nonScaryIncidentTypesNorthEast[8]))
                .append("\");\n");
        return result.toString();
    }

    private String determineColor() {
        return super.determineColor(score);
    }

}
