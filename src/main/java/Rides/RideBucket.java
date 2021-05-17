package Rides;

import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import geobroker.Location;
import geobroker.Raster;
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
    boolean matchedToSegment = true;
    String rideName;

    RideBucket(double lat, double lon, long timestamp, HashMap<String, Segment> segmentMap, Raster raster, ArrayList<Segment> visitedSegments, String pathToRide, Ride owner) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.rideName = Paths.get(pathToRide).getFileName().toString();
        this.segment = findSegment(segmentMap,raster, rideName);
        if (this.segment != null) {
            this.segment.rides.add(owner);
        }
    }

    private Segment findSegment(HashMap<String, Segment> segmentMap, Raster raster, String rideName) {
        Junction nearestJunction = null;
        double distanceToNearestJunction = Double.MAX_VALUE;
        Street nearestStreet = null;
        double distanceToNearestStreet = Double.MAX_VALUE;
        Location location = new Location(lat, lon);
        // contains all segments that are near the RideBucket
        List<ImmutablePair<String, String>> segmentCandidates = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location);

        // loop through all segment candidates and find the segment in which the RideBucket lies
        // Junction beats Street if RideBucket lies in both
        for (int i = 0; i < segmentCandidates.size(); i++) {

            // calculate distance to actual segment
            Segment actualSegment = segmentMap.get(segmentCandidates.get(i).getRight());
            double distance = calculateDistanceFromPointToPolygon(actualSegment,lat,lon);

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
        }

        if (nearestJunction == null && nearestStreet == null && segmentCandidates.size() > 0) {
            matchedToSegment = false;
        }
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
}
