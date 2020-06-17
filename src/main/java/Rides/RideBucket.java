package Rides;

import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import de.hasenburg.geobroker.commons.model.spatial.Location;
import de.hasenburg.geobroker.server.storage.Raster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Config.Config.*;
import static Leaflet.LeafletPrinter.*;

public class RideBucket {
    public double lat, lon;
    public long timestamp;
    Segment segment;
    public String rideName;
    boolean matchedToSegment = true;

    public RideBucket (double lat, double lon, long timestamp, HashMap<String, Segment> segmentMap, Raster raster, ArrayList<Segment> visitedSegments, String pathToRide) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.rideName = pathToRide.split("\\\\")[5];
        this.segment = findSegment(segmentMap,raster, rideName);
        /*
        Location location = new Location(lat, lon);
        List<ImmutablePair<String, String>> segmentsContainingRideBucket = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location);
        // junctions and streets overlap. If a location is in both segments, take the junction as segment
        if (segmentsContainingRideBucket.size()>0){
            System.out.println("Ride: " + pathToRide);
            System.out.println("RiceBucket: " + lat + "," + lon + "," + timestamp);
            System.out.println("segmentsContainingRideBucket size: " + segmentsContainingRideBucket.size());
            for (int i = 0; i < segmentsContainingRideBucket.size(); i++) {
                ImmutablePair<String, String> thisPair = segmentsContainingRideBucket.get(i);
                Segment segment = segmentMap.get(segmentsContainingRideBucket.get(i).getRight());
                //boolean found = isInPolygon(segment.poly_vertices_latsArray,segment.poly_vertices_lonsArray,lat,lon);
                //if (found) {
                //    System.out.println(lat + "," + lon + " found in " + segment.getClass() + " " + Arrays.toString(segment.poly_vertices_latsArray) + " and " + Arrays.toString(segment.poly_vertices_lonsArray));
                //}
            }

        }
        for (int i = 0; i < segmentsContainingRideBucket.size(); i++) {
            String segmentMapKey = segmentsContainingRideBucket.get(i).getRight();
            Segment entrySegment = segmentMap.get(segmentMapKey);
            segment = entrySegment;

            // System.out.println(lat + "," + lon + " matched segment with id " + segment.id + " and coordinates " + Arrays.toString(segment.lats) + " ; " + Arrays.toString(segment.lons) +  " is junction: " + segment.isJunction);

            if (entrySegment instanceof Junction) {
                break;
            }
        }
       */
    }

    private Segment findSegment(HashMap<String, Segment> segmentMap, Raster raster, String rideName) {
        Junction nearestJunction = null;
        double distanceToNearestJunction = Double.MAX_VALUE;
        Street nearestStreet = null;
        double distanceToNearestStreet = Double.MAX_VALUE;
        Location location = new Location(lat, lon);
        // contains all segments that are near the RideBucket
        List<ImmutablePair<String, String>> segmentCandidates = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location);
        /*
        if (segmentCandidates.size() == 0) {
            matchedToSegment = false;
        }
         */
        StringBuilder debug = new StringBuilder();
        // loop through all segment candidates and find the segment in which the RideBucket lies
        // Junction beats Street if RideBucket lies in both
        for (int i = 0; i < segmentCandidates.size(); i++) {

            // calculate distance to actual segment
            Segment actualSegment = segmentMap.get(segmentCandidates.get(i).getRight());
            double distance = calculateDistanceFromPointToPolygon(actualSegment,lat,lon);// distanceOp.distance();

            // update nearest junction / street and their distances respectively
            if (actualSegment instanceof Junction && distance <= distanceToNearestJunction) {
                if (distance <= MATCH_THRESHOLD) {
                    nearestJunction = (Junction) actualSegment;
                    distanceToNearestJunction = distance;
                }
            } else if (actualSegment instanceof Street && distance <= distanceToNearestStreet) {
                if (distance <= MATCH_THRESHOLD) {
                    nearestStreet = (Street) actualSegment;
                    distanceToNearestStreet = distance;

                }
            }
            debug.append(leafletPolygon(actualSegment.poly_vertices_latsArray, actualSegment.poly_vertices_lonsArray, "distance: " + distance))
                    .append(leafletMarker(lat, lon, rideName, timestamp, "distanceToNearestJunction: " + distanceToNearestJunction + "<br>distanceToNearestStreet: " + distanceToNearestStreet + "<br> MATCH_THRESHOLD: " + MATCH_THRESHOLD));

            /*
            if (timestamp == 1565680041553L) {
                System.out.println("distance: " + distance + " MATCH_THRESHOLD: " + MATCH_THRESHOLD);
                System.out.println("distance < MATCH_THRESHOLD: " + (distance < MATCH_THRESHOLD));
                debug.append(leafletPolygon(polyLats, polyLons, "distance: " + distance))
                        .append(leafletMarker(lat, lon, path, timestamp, "distanceToNearestJunction: " + distanceToNearestJunction + "<br>distanceToNearestStreet: " + distanceToNearestStreet + "<br> MATCH_THRESHOLD: " + MATCH_THRESHOLD));
            }
            */
        }

        if (nearestJunction == null && nearestStreet == null && segmentCandidates.size() > 0) {
            matchedToSegment = false;
            // debugSegments(segmentCandidates,segmentMap,path,distanceToNearestJunction,distanceToNearestStreet);
        }
        /*
        if (!matchedToSegment) {
            String path = PATH + "//Debug//" + rideName + "_" + timestamp + ".html";
            writeLeafletHTML(debug.toString(), path, lat + "," + lon);
            try {
                System.out.println("opening " + path);
                Desktop.getDesktop().browse(new File(path).toURI());
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        // return nearest segment
        if (distanceToNearestJunction <= distanceToNearestStreet) {
            return nearestJunction;
        } else {
            return nearestStreet;
        }
    }

    private double calculateDistanceFromPointToPolygon(Segment actualSegment, double lat, double lon) {
        double[] polyLats = actualSegment.poly_vertices_latsArray;
        double[] polyLons = actualSegment.poly_vertices_lonsArray;
        Coordinate[] polygonCoordinates = new Coordinate[polyLats.length];
        for (int j = 0; j < polyLats.length; j++) {
            polygonCoordinates[j] = new Coordinate(polyLats[j], polyLons[j]);
        }
        Polygon polygon = new GeometryFactory().createPolygon(polygonCoordinates);
        Point point = new GeometryFactory().createPoint(new Coordinate(lat, lon));
        DistanceOp distanceOp = new DistanceOp(polygon, point);
        return distanceOp.distance();
    }


    private void debugSegments(List<ImmutablePair<String, String>> segmentsContainingRideBucket, HashMap<String, Segment> segmentMap, String path, double distanceToNearestJunction, double distanceToNearestStreet) {

        StringBuilder debugString = new StringBuilder();
        debugString.append(leafletHead(lat + "," + lon));
        debugString.append("\t\tL.marker([")
                .append(lat).append(",").append(lon).append("]).addTo(map)")
                .append(".bindPopup(\"")
                .append(" distanceToNearestJunction: ")
                .append(distanceToNearestJunction)
                .append(" distanceToNearestStreet: ")
                .append(distanceToNearestStreet)
                .append("\");\n");
            for (int i = 0; i < segmentsContainingRideBucket.size(); i++) {
            Segment segment = segmentMap.get(segmentsContainingRideBucket.get(i).getRight());
            double[] polyLats = segment.poly_vertices_latsArray;
            double[] polyLons = segment.poly_vertices_lonsArray;
            Coordinate[] polygonCoordinates = new Coordinate[polyLats.length];
            debugString.append("\t\tL.polygon([\n");
            for (int j = 0; j < polyLats.length; j++) {
                polygonCoordinates[j] = new Coordinate(polyLats[j],polyLons[j]);
                debugString.append("\t\t\t[")
                        .append(polyLats[j]).append(",")
                        .append(polyLons[j]).append("],\n");
            }
                debugString.append("\t\t]).addTo(map)")
                        .append(".bindPopup(\"")
                        .append(segment.id)
                        .append("\");\n");

            }
        debugString.append("    </script>\n" +
                "</body>\n" +
                "</html>");
        try {
            // System.out.println("writing to: " + DEBUG_PATH + "\\" + path + "_" + timestamp + ".html");
            Files.write(Paths.get(DEBUG_PATH + "\\" + path + "_" + timestamp + ".html"), debugString.toString().getBytes(),StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    private static boolean isInPolygon(double[] polygonLats, double[] polygonLons, double lat, double lon) {
        Coordinate[] polygonCoordinates = new Coordinate[polygonLats.length];
        for (int i = 0; i < polygonLats.length; i++) {
            polygonCoordinates[i] = new Coordinate(polygonLats[i],polygonLons[i]);
        }
        Polygon polygon = new GeometryFactory().createPolygon(polygonCoordinates);
        Point point = new GeometryFactory().createPoint(new Coordinate(lat, lon));
        DistanceOp distanceOp = new DistanceOp(polygon,point);
        double distance = distanceOp.distance();
        System.out.println("distance: " + distance);
        Path2D polygon1 = new Path2D.Double();

        polygon1.moveTo(polygonLats[0], polygonLons[0]);
        for(int i = 1; i < polygonLats.length; ++i) {
            polygon1.lineTo(polygonLats[i], polygonLons[i]);
        }
        polygon1.lineTo(polygonLats[0], polygonLons[0]);
        //polygon1.closePath();
        return polygon1.contains(lat,lon);

    } */
}
