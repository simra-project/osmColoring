import de.hasenburg.geobroker.commons.model.spatial.Geofence;
import de.hasenburg.geobroker.commons.model.spatial.GeofenceKt;
import de.hasenburg.geobroker.commons.model.spatial.Location;
import de.hasenburg.geobroker.server.storage.Raster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        Location location1 = new Location(34.3, 12.4);
        System.out.println("location1: " + location1);
        Raster raster = new Raster(10);
        System.out.println("number of existing raster entries after creation: " + raster.getNumberOfExistingRasterEntries());
        Geofence geofence1 = Geofence.Companion.circle(location1, 0.5);
        raster.putSubscriptionIdIntoRasterEntries(geofence1,new ImmutablePair<>("", "123"));
        Location location2 = new Location(33.3, 11.4);
        Geofence geofence2 = Geofence.Companion.circle(location2, 0.5);
        raster.putSubscriptionIdIntoRasterEntries(geofence2,new ImmutablePair<>("","456"));
        System.out.println("number of existing raster entries after putting geofence in it: " + raster.getNumberOfExistingRasterEntries());
        System.out.println("subscription ids in raster entry for publisher location1 " + location1 + ": " + raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location1));
        System.out.println("subscription ids in raster entry for publisher location2 " + location2 + ": " + raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location2).getClass());
        List<ImmutablePair<String, String>> bla = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(location2);
        System.out.println(bla.get(0).getRight().getClass());
    }
}
