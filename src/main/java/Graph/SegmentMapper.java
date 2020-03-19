package Graph;

import Rides.Ride;
import de.hasenburg.geobroker.server.storage.Raster;

import java.io.File;
import java.util.*;

public class SegmentMapper {
    private static final String PATH = "C:\\Users\\ahmet\\Desktop\\SimRa\\";
    static List<Ride> rides = new ArrayList<>();

    public static void main(String[] args) {

        Raster raster = new Raster(1000);
        HashMap<String,Segment> segmentMap = SegmentImporter.importSegments(raster);
        File rideFolder[] = new File(PATH + "Berlin\\Rides").listFiles();
        if (rideFolder == null) {
            System.err.println("folder at " + PATH + "Berlin\\Rides is empty");
            return;
        }

        // loop through all rides
        for(int i = 0; i < rideFolder.length; i++) {
            rides.add(new Ride(rideFolder[i].getAbsolutePath(),segmentMap,raster));
        }

        //System.out.println(segmentMap.toString());
        double[][] coordsLat = new double[segmentMap.size()][];
        double[][] coordsLon = new double[segmentMap.size()][];
        String[] popUpTexts = new String[segmentMap.size()];

        int i = 0;
        for (Map.Entry<String,Segment> stringSegmentEntry : segmentMap.entrySet()) {
            Segment segment = stringSegmentEntry.getValue();
            coordsLat[i] = segment.poly_vertices_latsArray;
            coordsLon[i] = segment.poly_vertices_lonsArray;
            popUpTexts[i] = "junction: " + segment.isJunction + "<br>id: " + segment.id + "<br>highwayName: "
                    + Arrays.toString(segment.highwayName) + "<br>position: " + Arrays.toString(segment.lats)
                    + "<br>" + Arrays.toString(segment.lons);
            if (segment.isJunction) {
                popUpTexts[i] += "<br>rides: " + segment.numberOfRides;
                popUpTexts[i] += "<br>incidents: " + segment.numberOfIncidents;
            } else {
                popUpTexts[i] += "<br>numberOfRidesSouthWest: " + segment.numberOfRidesSouthWest;
                popUpTexts[i] += "<br>numberOfRidesNorthEast: " + segment.numberOfRidesNorthEast;
                popUpTexts[i] += "<br>numberOfIncidentsSouthWest: " + segment.numberOfIncidentsSouthWest;
                popUpTexts[i] += "<br>numberOfIncidentsNortheast: " + segment.numberOfIncidentsNortheast;
            }
            i++;
        }
        System.out.println(generateMapHTML(coordsLat,coordsLon,popUpTexts));

        /*
        coordsLat[0][0] = "52.500533";
        coordsLat[0][1] = "52.500533";
        coordsLat[0][2] = "52.531721";
        coordsLat[0][3] = "52.531721";

        coordsLat[1][0] = "51.500533";
        coordsLat[1][1] = "51.500533";
        coordsLat[1][2] = "51.531721";
        coordsLat[1][3] = "51.531721";


        coordsLon[0][0] = "13.281296";
        coordsLon[0][1] = "13.345498";
        coordsLon[0][2] = "13.345498";
        coordsLon[0][3] = "13.281296";

        coordsLon[1][0] = "12.281296";
        coordsLon[1][1] = "12.345498";
        coordsLon[1][2] = "12.345498";
        coordsLon[1][3] = "12.281296";
        */

        /*
        System.out.println(segmentMap.size());
        int numberOfSegmentsWithoutNeighbors = 0;
        for (Map.Entry<String, Graph.Segment> longSegmentEntry : segmentMap.entrySet()) {
            String entryId = longSegmentEntry.getKey();
            Graph.Segment entrySegment = (Graph.Segment) ((Map.Entry) longSegmentEntry).getValue();
            // System.out.println(entrySegment.toString());
            System.out.print("isJunction:" + entrySegment.isJunction);
            System.out.print("|entryId:" + entryId);
            if(entrySegment.neighbors.size()>1) {
                System.out.print("|neighbors:[" + entrySegment.id + ",");
                for (int i = 0; i < entrySegment.neighbors.size(); i++) {
                    System.out.print(entrySegment.neighbors.get(i).id + ",");
                }
                System.out.print(entrySegment.neighbors.get(entrySegment.neighbors.size()-1).id);
                System.out.println("]");

            } else if (entrySegment.neighbors.size()>0) {
                System.out.println("|neighbors:["+entrySegment.neighbors.get(0).id + "]");
            } else {
                System.out.println("segment without neighbor: " + entrySegment.id);
                numberOfSegmentsWithoutNeighbors++;
            }
        }
        System.out.println("numberOfSegmentsWithoutNeighbors: " + numberOfSegmentsWithoutNeighbors);
        */
    }

    public static String generateMapHTML(double[][] lats, double[][] lons, String[] popUpText) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Full Screen Leaflet Map</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <link rel=\"stylesheet\" href=\"https://d19vzq90twjlae.cloudfront.net/leaflet-0.7/leaflet.css\" />\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "\n" +
                "    <script src=\"https://d19vzq90twjlae.cloudfront.net/leaflet-0.7/leaflet.js\">\n" +
                "    </script>\n" +
                "\n" +
                "    <script>\n" +
                "        var map = L.map('map').setView([52.51616, 13.31339], 14);\n" +
                "        mapLink = \n" +
                "            '<a href=\"http://openstreetmap.org\">OpenStreetMap</a>';\n" +
                "        L.tileLayer(\n" +
                "            'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; ' + mapLink + ' Contributors',\n" +
                "            maxZoom: 18,\n" +
                "            }).addTo(map);\n" +
                "\t\t\t\n");
        for (int i = 0; i < lats.length-1; i++) {
            html.append("\t\tL.polygon([\n");
            for (int j = 0; j < lats[i].length-1; j++) {
                html.append("\t\t\t[")
                        .append(lats[i][j]).append(",").append(lons[i][j]).append("],\n");
            }
            html.append("\t\t],{color: '#4287f5'}).addTo(map).bindPopup(\"").append(popUpText[i]).append("\");\n");
        }
        html.append("\t\tvar popup = L.popup();\n" +
                "\n" +
                "\tfunction onMapClick(e) {\n" +
                "\t\tpopup\n" +
                "\t\t\t.setLatLng(e.latlng)\n" +
                "\t\t\t.setContent(\"You clicked the map at \" + e.latlng.toString())\n" +
                "\t\t\t.openOn(map);\n" +
                "\t}\n" +
                "\n" +
                "\tmap.on('click', onMapClick);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>");
        return html.toString();
    }

}
