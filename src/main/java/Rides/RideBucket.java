package Rides;

import Graph.Segment;
import de.hasenburg.geobroker.commons.model.spatial.Location;
import de.hasenburg.geobroker.server.storage.Raster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RideBucket {
    double lat, lon;
    long timestamp;
    Segment segment;

    public RideBucket (double lat, double lon, long timestamp, HashMap<String, Segment> segmentMap, Raster raster, ArrayList<Segment> visitedSegments) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        Location location = new Location(lat, lon);
        List<ImmutablePair<String, String>> segmentsContainingRideBucket = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location);
        // junctions and streets overlap. If a location is in both segments, take the junction as segment
        if (segmentsContainingRideBucket.size()>0){
            // System.out.println("segmentsContainingRideBucket size: " + segmentsContainingRideBucket.size());
        }
        for (int i = 0; i < segmentsContainingRideBucket.size(); i++) {
            String segmentMapKey = segmentsContainingRideBucket.get(i).getRight();
            Segment entrySegment = segmentMap.get(segmentMapKey);
            segment = entrySegment;

            // System.out.println(lat + "," + lon + " matched segment with id " + segment.id + " and coordinates " + Arrays.toString(segment.lats) + " ; " + Arrays.toString(segment.lons) +  " is junction: " + segment.isJunction);

            if (entrySegment.isJunction) {
                break;
            }
        }
    }
}
