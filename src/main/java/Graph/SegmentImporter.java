package Graph;

import de.hasenburg.geobroker.server.storage.Raster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.*;
import java.util.*;

import static java.util.List.of;

public class SegmentImporter {

    private static String pathToJunctionsdf = "C:\\Users\\ahmet\\Desktop\\junctionsdf.csv";
    private static String pathToSegments = "C:\\Users\\ahmet\\Desktop\\segmentsdf.csv";
    private static Map<String,Segment> segmentMap = new HashMap<>();
    public static HashMap<String,Segment> importSegments(Raster raster) {
        // read the junctions and put the into segmentMap
        System.out.println("importing junctions from: " + pathToJunctionsdf);
        File file = new File(pathToJunctionsdf);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header id|lat|lon|highwayName|highwaytypes|highwaylanes|poly_vertices_lats|poly_vertices_lons
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                //String[] junctionLineArray = splitButIgnoreQuotes(line);
                String[] junctionLineArray = line.split("\\|",-1);
                String id = junctionLineArray[0].split(" ,")[0] + ".-1";
                String[] latsAsStrings = junctionLineArray[1].split(", ");
                double[] lats = convertStringArrayToDoubleArray(latsAsStrings);
                String[] lonsAsStrings = junctionLineArray[2].split(", ");
                double[] lons = convertStringArrayToDoubleArray(lonsAsStrings);
                String[] highwayNamesArray = junctionLineArray[3]
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                String[] highWayTypesArray = junctionLineArray[4]
                        .replaceAll("\"","")
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                String[] highWayLanesStrings = junctionLineArray[5]
                        .replaceAll("\"","")
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                int[] highWayLanesArray = new int[highWayLanesStrings.length];
                for (int i = 0; i < highWayLanesStrings.length; i++) {
                    if(highWayLanesStrings[i].equals("unknown")) {
                        highWayLanesArray[i] = -1;
                    } else {
                        highWayLanesArray[i] = Integer.valueOf(highWayLanesStrings[i]);
                    }
                }
                String[] polyLatsStrings = junctionLineArray[6]
                        .replace("array('d', ","")
                        .replace("[","")
                        .replace("]","")
                        .replace(")","")
                        .replaceAll("'","")
                        .split(", ");
                double[] polyLatsArray = convertStringArrayToDoubleArray(polyLatsStrings);
                String[] polyLonsStrings = junctionLineArray[7]
                        .replace("array('d', ","")
                        .replace("[","")
                        .replace("]","")
                        .replace(")","")
                        .replaceAll("'","")
                        .split(", ");
                double[] polyLonsArray = convertStringArrayToDoubleArray(polyLonsStrings);
                Segment segment = new Segment(id,lats,lons,highwayNamesArray,highWayTypesArray,highWayLanesArray,polyLatsArray,polyLonsArray);
                segmentMap.put(id, segment);
                raster.putSubscriptionIdIntoRasterEntries(segment.geofence, new ImmutablePair<>("", id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("segmentMap size after adding junctions: " + segmentMap.size());
        // read the streets and put the into segmentMap
        System.out.println("importing street segments from: " + pathToSegments);
        file = new File(pathToSegments);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header id|segment_nr|lats|lons|highwayName|highwaytype|highwaylanes|poly_vertices_lats|poly_vertices_lons
            String line = br.readLine();
            while ((line = br.readLine()) != null) {

                String[] streetsLineArray = line.split("\\|",-1);
                String id =  streetsLineArray[1];
                if (!id.contains(".")) {
                    id += ".0";
                }
                String segment_nr = id;
                String[] latsStrings = streetsLineArray[2]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");
                double[] latsArray = convertStringArrayToDoubleArray(latsStrings);
                String[] lonsStrings = streetsLineArray[3]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");
                double[] lonsArray = convertStringArrayToDoubleArray(lonsStrings);
                String highwayname = streetsLineArray[4];
                String[] highWayTypesArray = {streetsLineArray[5]};
                int highwaylanes = -1;
                if (streetsLineArray[6].length()>0) {
                    highwaylanes = Integer.valueOf(streetsLineArray[6]);
                }
                int lanes_Backward = -1;

                if (streetsLineArray[7].length()>0) {
                    lanes_Backward = Integer.valueOf(streetsLineArray[7]);
                }
                String[] segment_nodesStrings = streetsLineArray[8]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");
                /*
                Integer[] segment_nodesArray = new Integer[segment_nodesStrings.length];
                for (int i = 0; i < segment_nodesArray.length; i++) {
                    if (segment_nodesStrings[i].length() < 3) {
                        continue;
                    }
                    segment_nodesArray[i] = Integer.valueOf(segment_nodesStrings[i].substring(2));
                }
                */
                String[] poly_vertices_latsStrings = streetsLineArray[9]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");
                double[] poly_vertices_latsArray = convertStringArrayToDoubleArray(poly_vertices_latsStrings);
                String[] poly_vertices_lonsStrings = streetsLineArray[10]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");
                double[] poly_vertices_lonsArray = convertStringArrayToDoubleArray(poly_vertices_lonsStrings);
                Segment streetSegment = new Segment(id,segment_nr,latsArray,lonsArray,highwayname,highWayTypesArray,highwaylanes,lanes_Backward,segment_nodesStrings,poly_vertices_latsArray,poly_vertices_lonsArray);

                if (id.startsWith("100541440")) {
                    System.out.println(id);
                    System.out.println("isJunction: " + streetSegment.isJunction);
                    System.out.println(streetSegment.geofence.toString());
                }

                linkStreetSegmentToJunction(streetSegment,(HashMap<String, Segment>)segmentMap);
                if(segmentMap.containsKey(segment_nr)) {
                    System.out.println(id + " already in segmentMap");
                }
                segmentMap.put(segment_nr, streetSegment);
                raster.putSubscriptionIdIntoRasterEntries(streetSegment.geofence, new ImmutablePair<>("", id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (HashMap<String,Segment>)segmentMap;
    }

    private static double[] convertStringArrayToDoubleArray(String[] inputArray) {
        double[] result = new double[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            result[i] = Double.valueOf(inputArray[i]);
        }
        return result;
    }

    private static void linkStreetSegmentToJunction(Segment thisStreetSegment, HashMap<String,Segment> segmentMap) {

        for (Map.Entry<String, Segment> longSegmentEntry : segmentMap.entrySet()) {
            String entryId = (String) ((Map.Entry) longSegmentEntry).getKey();
            Segment entrySegment = (Segment) ((Map.Entry) longSegmentEntry).getValue();
            // skip if entrySegment is a street
            if (entrySegment.segment_nodes != null) {
                continue;
            }
            for (int i = 0; i < thisStreetSegment.segment_nodes.length; i++) {
                // System.out.println("thisStreetSegment.segment_nodes["+i+"]: " + thisStreetSegment.segment_nodes[i] + " entryId.split(\",\")[0]): " + entryId.split(",")[0]);
                String[] entryIds = entryId.split(", ");
                for (int j = 0; j < entryIds.length; j++) {
                    // System.out.println(thisStreetSegment.segment_nodes[i] + "==" + entryIds[j]);
                    if (thisStreetSegment.segment_nodes[i].equals(entryIds[j].split("\\.")[0])) {
                        // System.out.println("thisStreetSegment.segment_nodes["+i+"]: " + thisStreetSegment.segment_nodes[i] + " entryId.split(\",\")[0]): " + entryId.split(",")[0]);
                        thisStreetSegment.addNeighbor(entrySegment);
                        entrySegment.addNeighbor(thisStreetSegment);
                        break;
                    }
                }
            }
        }
        // System.out.println("number of neighbors of segment with id: " + thisStreetSegment.id + ": " + thisStreetSegment.neighbors.size());
    }
    /*
    private static double[] fillLocationArray(String[] polyLatsStrings) {
        double[] polyLatsArray = new double[polyLatsStrings.length];
        for (int i = 0; i < polyLatsStrings.length - 1; i++) {
            polyLatsArray[i] = Double.valueOf(polyLatsStrings[i].replace("\"",""));
        }
        return polyLatsArray;
    }
    */

    private static String[] splitButIgnoreQuotes(String input){
        List<String> result = new ArrayList<String>();
        int start = 0;
        boolean inQuotes = false;
        for (int current = 0; current < input.length(); current++) {
            if (input.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
            boolean atLastChar = (current == input.length() - 1);
            if(atLastChar) result.add(input.substring(start));
            else if (input.charAt(current) == ',' && !inQuotes) {
                result.add(input.substring(start, current));
                start = current + 1;
            }
        }
        return result.toArray(new String[0]);
    }
}
