package Segments;

import geobroker.Geofence;
import geobroker.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hexagon extends Segment implements Comparable<Hexagon>{
    public int numberOfRides, numberOfIncidents, numberOfScaryIncidents, numberOfNonScaryIncidents;
    public int clopa, spiot, nlorh, ssho, tailgating, near_dooring, dao, other;
    public HashMap<String, Integer> scaryIncidentTypes, nonScaryIncidentTypes = new HashMap<>();
    public double dangerousnessScore;


    public Hexagon(String id, double[] lats, double[] lons, double[] polyLats, double[] polyLons) {
        this.id = id;
        this.lats = lats;
        this.lons = lons;
        this.poly_vertices_latsArray = polyLats;
        this.poly_vertices_lonsArray = polyLons;
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < polyLats.length-2; i++) {
            locations.add(new Location(polyLats[i],polyLons[i]));
        }
        this.geofence = Geofence.Companion.polygon(locations);
        scaryIncidentTypes = new HashMap<>();
        scaryIncidentTypes.put("-2",0);
        scaryIncidentTypes.put("1",0);
        scaryIncidentTypes.put("2",0);
        scaryIncidentTypes.put("3",0);
        scaryIncidentTypes.put("4",0);
        scaryIncidentTypes.put("5",0);
        scaryIncidentTypes.put("6",0);
        scaryIncidentTypes.put("7",0);
        scaryIncidentTypes.put("8",0);
        nonScaryIncidentTypes = new HashMap<>();
        nonScaryIncidentTypes.put("-2",0);
        nonScaryIncidentTypes.put("1",0);
        nonScaryIncidentTypes.put("2",0);
        nonScaryIncidentTypes.put("3",0);
        nonScaryIncidentTypes.put("4",0);
        nonScaryIncidentTypes.put("5",0);
        nonScaryIncidentTypes.put("6",0);
        nonScaryIncidentTypes.put("7",0);
        nonScaryIncidentTypes.put("8",0);
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

        result.append("HEXAGON:");
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

    public void appendFeatures(StringBuilder result){
        result.append("{\"type\":\"Feature\",\"id\":\"").append(id)
                .append("\",\"properties\":{\"type\":\"Hexagon\",")
                .append("\n\"score\":").append(getScore())
                .append(",\n\"incidents\":").append((numberOfNonScaryIncidents + numberOfScaryIncidents))
                .append(",\n\"scary incidents\":").append((numberOfScaryIncidents))
                .append(",\n\"non-scary incidents\":").append((numberOfNonScaryIncidents))
                .append(",\n\"rides\":").append(numberOfRides)
                .append(",\n\"clopa\":").append((nonScaryIncidentTypes.get("-2") + scaryIncidentTypes.get("-2") + nonScaryIncidentTypes.get("1") + scaryIncidentTypes.get("1") + clopa))
                .append(",\n\"spiot\":").append((nonScaryIncidentTypes.get("2") + scaryIncidentTypes.get("2")+spiot))
                .append(",\n\"nlorh\":").append((nonScaryIncidentTypes.get("3") + scaryIncidentTypes.get("3")+nlorh))
                .append(",\n\"ssho\":").append((nonScaryIncidentTypes.get("4") + scaryIncidentTypes.get("4")+ssho))
                .append(",\n\"tailgating\":").append((nonScaryIncidentTypes.get("5") + scaryIncidentTypes.get("5")+tailgating))
                .append(",\n\"near-dooring\":").append((nonScaryIncidentTypes.get("6") + scaryIncidentTypes.get("6")+near_dooring))
                .append(",\n\"dao\":").append((nonScaryIncidentTypes.get("7") + scaryIncidentTypes.get("7")+dao))
                .append(",\n\"other\":").append((nonScaryIncidentTypes.get("8") + scaryIncidentTypes.get("8")+other));
    }
    public String detailJson() {
        StringBuilder result = new StringBuilder();
        appendFeatures(result);
        //Lats
        result.append(",\n\"lats\":[");
        for (int i = 0; i < lats.length; i++){
            result.append(lats[i]);
            if (i != lats.length - 1){
                result.append(",");
            }
        }
        result.append("]");
        //Lons
        result.append(",\n\"lons\":[");
        for (int i = 0; i < lons.length; i++){
            result.append(lons[i]);
            if (i != lons.length - 1){
                result.append(",");
            }
        }
        result.append("]");

        //Poly_lats
        result.append(",\n\"poly lats\":[");
        for (int i = 0; i < poly_vertices_latsArray.length; i++){
            result.append(poly_vertices_latsArray[i]);
            if (i != poly_vertices_latsArray.length - 1){
                result.append(",");
            }
        }
        result.append("]");
        //Poly lons
        result.append(",\n\"poly lons\":[");
        for (int i = 0; i < poly_vertices_lonsArray.length; i++){
            result.append(poly_vertices_lonsArray[i]);
            if (i != poly_vertices_lonsArray.length - 1){
                result.append(",");
            }
        }
        result.append("]");
        result.append(super.toGeoJson());
        result.append("},\n\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[");
        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            result.append("[").append(poly_vertices_lonsArray[i]).append(",").append(poly_vertices_latsArray[i]).append("],");
        }
        result.append("[").append(poly_vertices_lonsArray[poly_vertices_lonsArray.length-1]).append(",").append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append("]]]}}");
        return result.toString();
    }
    public String toGeoJson() {
        StringBuilder result = new StringBuilder();
        appendFeatures(result);
        result.append(super.toGeoJson())
                .append("},\n\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[");

        for (int i = 0; i < poly_vertices_latsArray.length-1; i++) {
            result.append("[").append(poly_vertices_lonsArray[i]).append(",").append(poly_vertices_latsArray[i]).append("],");
        }
        result.append("[").append(poly_vertices_lonsArray[poly_vertices_lonsArray.length-1]).append(",").append(poly_vertices_latsArray[poly_vertices_latsArray.length-1]).append("]]]}}");

        return result.toString();
    }

    @Override
    public int compareTo(@NotNull Hexagon o) {
        return this.getScore().compareTo(o.getScore());
    }
}
