package Graph;

import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import geobroker.Raster;
import main.CommandLineArguments;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class SegmentImporter {

    private static Logger logger = LogManager.getLogger();

    private static Map<String,Segment> segmentMap = new HashMap<>();
    public static HashMap<String,Segment> importSegments(Raster raster, CommandLineArguments cla) {
        // read the junctions and put the into segmentMap
        logger.info("Importing junctions from: " + cla.getOsmJunctionFile().getAbsolutePath());
        File file = cla.getOsmJunctionFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header id|lat|lon|highwayName|highwaytypes|highwaylanes|poly_vertices_lats|poly_vertices_lons
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] junctionLineArray = line.split("\\|",-1);
                String id = junctionLineArray[0].split(" ,")[0] + ".0";
                String[] latsAsStrings = junctionLineArray[1].split(", ");
                double[] lats = convertStringArrayToDoubleArray(latsAsStrings);
                String[] lonsAsStrings = junctionLineArray[2].split(", ");
                double[] lons = convertStringArrayToDoubleArray(lonsAsStrings);
                String[] highwayNamesArray = junctionLineArray[4]
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .replaceAll("\"","")
                        .split(", ");
                HashSet<String> highWayNamesHashSet = new HashSet<>(Arrays.asList(highwayNamesArray));
                String[] highWayTypesArray = junctionLineArray[5]
                        .replaceAll("\"","")
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                String[] highWayLanesStrings = junctionLineArray[6]
                        .replaceAll("\"","")
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                double[] highWayLanesArray = new double[highWayLanesStrings.length];
                for (int i = 0; i < highWayLanesStrings.length; i++) {
                    if(highWayLanesStrings[i].equals("unknown")) {
                        highWayLanesArray[i] = -1;
                    } else {
                        highWayLanesArray[i] = Double.valueOf(highWayLanesStrings[i]);
                    }
                }
                String[] lanes_bwStrings = junctionLineArray[7]
                        .replaceAll("\"","")
                        .replace("[","")
                        .replace("]","")
                        .replaceAll("'","")
                        .split(", ");
                double[] lanes_bwArray = new double[lanes_bwStrings.length];
                for (int i = 0; i < lanes_bwStrings.length; i++) {
                    if(lanes_bwStrings[i].equals("unknown")) {
                        lanes_bwArray[i] = -1.0;
                    } else {
                        lanes_bwArray[i] = Double.valueOf(lanes_bwStrings[i]);
                    }
                }
                String[] polyLatsStrings = junctionLineArray[9]
                        .replace("[","")
                        .replace("]","")
                        .replace("array('d', ", "")
                        .replace(")","")
                        .split(", ");
                double[] polyLatsArray = convertStringArrayToDoubleArray(polyLatsStrings);
                String[] polyLonsStrings = junctionLineArray[10]
                        .replace("[","")
                        .replace("]","")
                        .replace("array('d', ", "")
                        .replace(")","")
                        .split(", ");
                double[] polyLonsArray = convertStringArrayToDoubleArray(polyLonsStrings);
                Junction junction = new Junction(id,lats,lons,highWayNamesHashSet,highWayTypesArray,highWayLanesArray,lanes_bwArray,polyLatsArray,polyLonsArray);
                int segment_nr = 0;
                while (segmentMap.containsKey(id)) {
                    segment_nr++;
                    id = id.split("\\.")[0] + "." + segment_nr;
                }
                segmentMap.put(id, junction);
                raster.putSubscriptionIdIntoRasterEntries(junction.geofence, new ImmutablePair<>("", id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("SegmentMap size after adding junctions: " + segmentMap.size());
        // read the streets and put the into segmentMap
        logger.info("Importing street segments from: " + cla.getOsmSegmentsFile().getAbsolutePath());
        file = cla.getOsmSegmentsFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip header id|lats|lons|highwayName|highwaytype|highwaylanes|poly_vertices_lats|poly_vertices_lons
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] streetsLineArray = line.split("\\|",-1);
                String id =  streetsLineArray[1] + ".0";
                String highwayname = streetsLineArray[2].split(",",-1)[0].replaceAll("\"","");
                String[] highWayTypesArray = {streetsLineArray[3]};
                double highwaylanes = -1;
                if (streetsLineArray[4].length()==1) {
                    highwaylanes = Double.valueOf(streetsLineArray[4]);
                } else if (streetsLineArray[4].length()>1 && !streetsLineArray[4].contains("u")) {
                    highwaylanes = Double.valueOf(streetsLineArray[4].split(",")[0]);
                }

                String[] lanes_BackwardStrings = streetsLineArray[5]
                        .replace("unknown","")
                        .replace("[","")
                        .replace("}","")
                        .split(", ",-1);
                double[] lanes_Backward = new double[lanes_BackwardStrings.length];
                for (int i = 0; i < lanes_BackwardStrings.length; i++) {
                    if (lanes_BackwardStrings[i].length()>0) {
                        lanes_Backward[i] = Double.valueOf(lanes_BackwardStrings[i]);
                    }
                }
                if (lanes_Backward.length<1) {
                    lanes_Backward = new double[1];
                    lanes_Backward[0] = -1;
                }
                String[] segment_nodesStrings = streetsLineArray[6]
                        .replace("[","")
                        .replace("]","")
                        .split(", ");

                double seg_length = Double.valueOf(streetsLineArray[7]);
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
                Street streetSegment = new Street(id,highwayname,highWayTypesArray,highwaylanes,lanes_Backward,segment_nodesStrings,seg_length,poly_vertices_latsArray,poly_vertices_lonsArray);

                // linkStreetSegmentToJunction(streetSegment,(HashMap<String, Segment>)segmentMap);
                int segment_nr = 0;
                while (segmentMap.containsKey(id)) {
                    segment_nr++;
                    id = id.split("\\.")[0] + "." + segment_nr;
                }
                segmentMap.put(id, streetSegment);
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

            result[i] = Double.valueOf(inputArray[i].replaceAll("[()]", "").replaceAll("array'd'",""));

        }
        return result;
    }

    private static void linkStreetSegmentToJunction(Street thisStreetSegment, HashMap<String,Segment> segmentMap) {

        for (Map.Entry<String, Segment> segmentEntry : segmentMap.entrySet()) {
            String entryId = (String) ((Map.Entry) segmentEntry).getKey();
            Segment entryJunction = (Segment) ((Map.Entry) segmentEntry).getValue();
            // skip if entryJunction is a street
            if (entryJunction instanceof Junction) {
                Junction junction = (Junction) entryJunction;
                for (int i = 0; i < (thisStreetSegment).segment_nodes.length; i++) {
                    String[] entryIds = entryId.split(", ");
                    for (int j = 0; j < entryIds.length; j++) {
                        if ((thisStreetSegment).segment_nodes[i].equals(entryIds[j].split("\\.")[0])) {
                            thisStreetSegment.addNeighbor(junction);
                            junction.addNeighbor(thisStreetSegment);
                            break;
                        }
                    }
                }
            }
        }
    }
}
