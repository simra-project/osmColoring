package Graph;

import Segments.Hexagon;
import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import geobroker.Geofence;
import geobroker.Raster;
import main.CommandLineArguments;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.DefaultGridFeatureBuilder;
import org.geotools.grid.GridFeatureBuilder;
import org.geotools.grid.hexagon.HexagonOrientation;
import org.geotools.grid.hexagon.Hexagons;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

public class SegmentImporter {

    private static Logger logger = LogManager.getLogger();

    private static Map<String,Segment> segmentMap = new HashMap<>();
    public static HashMap<String,Segment> importSegments(Raster raster, CommandLineArguments cla) {
        // getting the bounding box coordinates to know the size of hexagon grid
        File osmMetaFile = cla.getOsmMetaFile();
        double[] bounding_box = getBoundingBox(osmMetaFile);
        double minX = bounding_box[0];
        double minY = bounding_box[1];
        double maxX = bounding_box[2];
        double maxY = bounding_box[3];

        // test
        List<Geometry> hexagons = createHexagonalGrid(bounding_box); // new function to create a hexagon grid
        for(Geometry g : hexagons){
            Coordinate[] vertices = g.getCoordinates();
            List<Double> latsArray = new ArrayList<>();
            List<Double> lonsArray = new ArrayList<>();
            for (Coordinate point : vertices){
                latsArray.add(point.getX());
                lonsArray.add(point.getY());
            }
            double[] polyLats = latsArray.stream().mapToDouble(Double::doubleValue).toArray();
            logger.debug("LATS: " + Arrays.toString(polyLats));
            double[] polyLons = lonsArray.stream().mapToDouble(Double::doubleValue).toArray();
            logger.debug("LONS: " + Arrays.toString(polyLons));
            String uniqueID = UUID.randomUUID().toString();
            Hexagon hexagon = new Hexagon(uniqueID, polyLats, polyLons, polyLats, polyLons);
            logger.debug(hexagon.geofence);
            segmentMap.put(uniqueID, hexagon);
            logger.debug("putting in raster...");
            // everything works until here idk y
            raster.putSubscriptionIdIntoRasterEntries(hexagon.geofence, new ImmutablePair<>("", uniqueID)); // this seems to be create a problem that idk
            logger.debug("done");
            System.exit(-1);
        }
        // end of test

        // read the junctions and put the into segmentMap
        File jsonDetailFile = cla.getJsonDetailOutputFile();
        JSONParser parser = new JSONParser();
        // read the streets and put the into segmentMap
        if(jsonDetailFile.exists()){
            try(FileReader reader = new FileReader(jsonDetailFile)){
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray features = (JSONArray) jsonObject.get("features");

                for(Object featureObj : features) {
                    JSONObject feature = (JSONObject) featureObj;
                    JSONObject properties = (JSONObject) feature.get("properties");

                    String type = (String) properties.get("type"); // the type of each segment: Junction or Street
                    String id = (String) feature.get("id"); // the id of each segment
                    if ("Hexagon".equalsIgnoreCase(type)) {
                        double[] lats = convertJSONArrayToDoubleArray((JSONArray) properties.get("lats"));
                        double[] lons = convertJSONArrayToDoubleArray((JSONArray) properties.get("lons"));
                        double[] polyLats = convertJSONArrayToDoubleArray((JSONArray) properties.get("poly lats"));
                        double[] polyLons = convertJSONArrayToDoubleArray((JSONArray) properties.get("poly lons"));

                        Hexagon hexagon = new Hexagon(id, lats, lons, polyLats, polyLons);
                        segmentMap.put(hexagon.id, hexagon);
                        raster.putSubscriptionIdIntoRasterEntries(hexagon.geofence, new ImmutablePair<>("", hexagon.id));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            List<List<Point2D.Double>> hexagonGrid = createHexagons(minX, minY, maxX, maxY);
            for (List<Point2D.Double> shape : hexagonGrid) {
                List<Double> latsArray = new ArrayList<>();
                List<Double> lonsArray = new ArrayList<>();

                for (Point2D.Double point : shape) {
                    latsArray.add(point.getY());
                    lonsArray.add(point.getX());
                }
                double[] polyLats = latsArray.stream().mapToDouble(Double::doubleValue).toArray();
                logger.debug("LATS: " + Arrays.toString(polyLats));
                double[] polyLons = lonsArray.stream().mapToDouble(Double::doubleValue).toArray();
                logger.debug("LONS: " + Arrays.toString(polyLons));
                String uniqueID = UUID.randomUUID().toString();
                Hexagon hexagon = new Hexagon(uniqueID, polyLats, polyLons, polyLats, polyLons);
                logger.debug(hexagon.geofence);
                segmentMap.put(uniqueID, hexagon);
                raster.putSubscriptionIdIntoRasterEntries(hexagon.geofence, new ImmutablePair<>("", uniqueID));
                System.exit(-1);
            }
        }

        logger.info("SegmentMap size after adding hexagons: " + segmentMap.size());

        return (HashMap<String,Segment>)segmentMap;
    }
    private static List<List<Point2D.Double>> createHexagons(double minX, double minY, double maxX, double maxY) {
        List<List<Point2D.Double>> hexagonGrid = new ArrayList<>();

        double width = maxX - minX;
        double height = maxY - minY;

        double hexagonSize = 0.03;
        double hDist = hexagonSize * Math.sqrt(3.0);
        double vDist = 3.0 * hexagonSize / 2.0;

        int numHorHexagon = (int) Math.floor(width / hDist);
        int numVerHexagon = (int) Math.floor(height / vDist);

        double startX = minX + (width - numHorHexagon * hDist) / 2.0;
        double startY = minY + (height - numVerHexagon * vDist) / 2.0;

        for (int row = 0; row < numVerHexagon; row++) {
            for (int col = 0; col < numHorHexagon; col++) {
                double offsetX = (row % 2 == 0) ? 0.0 : hDist / 2.0;
                double hexagonX = startX + col * hDist + offsetX;
                double hexagonY = startY + row * vDist;

                // Check if the hexagon extends beyond the bounding box
                if (hexagonX + hexagonSize > maxX || hexagonY + hexagonSize > maxY) {
                    continue; // Skip this hexagon as it is outside the bounding box
                }

                List<Point2D.Double> vertices = calculateHexagonVertices(hexagonX, hexagonY, hexagonSize);
                hexagonGrid.add(vertices);
            }
        }

        return hexagonGrid;
    }

    private static double[] convertStringArrayToDoubleArray(String[] inputArray) {

        double[] result = new double[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {

            result[i] = Double.valueOf(inputArray[i].replaceAll("[()]", "").replaceAll("array'd'",""));

        }
        return result;
    }

    private static void linkStreetSegmentToJunction(Street thisStreetSegment,HashMap<String,Segment> segmentMap) {

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
    private static double[] convertJSONArrayToDoubleArray(JSONArray jsonArray){
        double[] array = new double[jsonArray.size()];
        for(int i=0; i<jsonArray.size();i++){
            array[i] = (double) jsonArray.get(i);
        }
        return array;
    }

    private static double[] getBoundingBox(File file){
        double[] bounding_box = null;
        if(file.exists()){
            try (FileReader reader = new FileReader(file)){
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                bounding_box = convertJSONArrayToDoubleArray((JSONArray) jsonObject.get("bounding_box"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return bounding_box;
    }

    private static List<Geometry> createHexagonalGrid(double[] box){
        List<Geometry> hexagons = new ArrayList<>();
        CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
        ReferencedEnvelope gridBounds = new ReferencedEnvelope(box[1], box[0], box[3], box[2], sourceCRS);
        double sideLen = 5.0;
        GridFeatureBuilder builder = new DefaultGridFeatureBuilder();
        SimpleFeatureSource grid = Hexagons.createGrid(gridBounds, sideLen, HexagonOrientation.ANGLED, builder);
        try{
            SimpleFeatureCollection collection = grid.getFeatures();
            FeatureIterator iterator = collection.features();
            while (iterator.hasNext()){
                Feature feature = iterator.next();
                SimpleFeature sFeature = (SimpleFeature) feature;
                //logger.debug(sFeature.getAttribute(0));
                Geometry geometry = (Geometry) sFeature.getAttribute(0);
                hexagons.add(geometry);
//                Coordinate[] coordinates = geofence.getCoordinates();
//                logger.debug(Arrays.toString(coordinates));
               /* String uniqueID = UUID.randomUUID().toString();
                raster.putSubscriptionIdIntoRasterEntries(geofence, new ImmutablePair<>("", uniqueID));*/
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return hexagons;
    }
    //also need to calculate centre?
    public static List<Point2D.Double> calculateHexagonVertices(double centerX, double centerY, double size) {
        List<Point2D.Double> vertices = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI / 6.0 * (i + 0.5);
            double x = centerX + size * Math.cos(angle);
            double y = centerY + size * Math.sin(angle);
            vertices.add(new Point2D.Double(x, y));
        }
        return vertices;
    }

}
