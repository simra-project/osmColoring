package Graph;

import Rides.Incident;
import Rides.Ride;
import Rides.RideBucket;
import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import geobroker.Raster;

import java.io.File;
import java.util.*;

import static Leaflet.GeoJsonPrinter.writeGeoJSON;
import static Leaflet.LeafletPrinter.leafletMarker;
import static Rides.Ride.isInBoundingBox;
import static main.UtilKt.getRidesOfRegionAndUNKNOWN;

import main.CommandLineArguments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SegmentMapper {

    private static Logger logger = LogManager.getLogger();

    static List<Junction> mostDangerousJunctions = new ArrayList<>();
    static List<Street> mostDangerousStreetsSouthWest = new ArrayList<>();
    static List<Street> mostDangerousStreetsNorthEast = new ArrayList<>();
    static int numberOfRelevantSegments = 0;

    public static void doSegmentMapping(CommandLineArguments cla) {
        // Creating the Geobroker Raster. See the documentation of the Geobroker: https://github.com/MoeweX/geobroker
        Raster raster = new Raster(1000);

        // Contains the Junctions and Street Segments, including their statistics and dangerousness score
        // as value and their Id(s) as their key.
        HashMap<String, Segment> segmentMap = SegmentImporter.importSegments(raster, cla);

        // These Lists contain junctions and steet segments only. Their purpose is to sort them according to their
        // dangerousness score.
        List <Street> streetList = new ArrayList<>();
        List <Junction> junctionList = new ArrayList<>();

        // Contains the ride files of specified region.
        List<File> rideFolder = getRidesOfRegionAndUNKNOWN(cla.getSimraRoot(), cla.getRegion());
        StringBuilder content = new StringBuilder();
        int numberOfUnmatchedRideBuckets = 0;
        int numberOfAllRides = 0;
        int numberOfIncludedRides = 0;
        int numberOfMatchedIncidents = 0;
        int numberOfUnmatchedIncidents = 0;
        List<Incident> unmatchedIncidents = new ArrayList<>();
        StringBuilder mapContent = new StringBuilder();
        StringBuilder geoJSONContent = new StringBuilder();
        for (int i = 0; i < rideFolder.size(); i++) {
            Ride ride = new Ride(rideFolder.get(i).getPath(),segmentMap,raster, cla);
            if ( ride.rideBuckets.size() > 0 && isInBoundingBox(ride.rideBuckets.get(0).lat,ride.rideBuckets.get(0).lon,cla.getBBOX_LATS(),cla.getBBOX_LONS())) {
                numberOfIncludedRides++;
                numberOfMatchedIncidents += ride.numberOfMatchedIncidents;
                unmatchedIncidents.addAll(ride.unmatchedIncidents);
            }
            numberOfAllRides ++;
            ArrayList<RideBucket> unmatchedRideBuckets = ride.getUnmatchedRideBuckets();
            numberOfUnmatchedRideBuckets += unmatchedRideBuckets.size();
        }
        for (int i = 0; i < unmatchedIncidents.size(); i++) {
            Incident thisIncident = unmatchedIncidents.get(i);
            content.append(leafletMarker(thisIncident.lat,thisIncident.lon,thisIncident.rideName,thisIncident.timestamp));
        }

        logger.debug("number of unmatched ride buckets: " + numberOfUnmatchedRideBuckets);
        logger.debug("number of all rides: " + numberOfAllRides);
        logger.debug("number of included rides: " + numberOfIncludedRides);
        logger.debug("number of all incidents: " + (numberOfMatchedIncidents + unmatchedIncidents.size()));
        logger.debug("number of included incidents: " + numberOfMatchedIncidents);

        int numberOfSegmentsWithRides = 0;
        int segmentIndex = 0;
        boolean added = true; // stores wether a segment was added
        for (Map.Entry<String,Segment> stringSegmentEntry : segmentMap.entrySet()) {
            Segment segment = stringSegmentEntry.getValue();
            if (hasRide(segment)) {
                numberOfSegmentsWithRides++;
            }
            if (segment instanceof Junction) {
                Junction junction = (Junction) segment;
                if (mostDangerousJunctions.size() < 3) {
                    mostDangerousJunctions.add(junction);
                    mostDangerousJunctions.add(junction);
                    mostDangerousJunctions.add(junction);
                }
                junction.dangerousnessScore = ((cla.getScarinessFactor() * junction.numberOfScaryIncidents + junction.numberOfNonScaryIncidents) /
                        junction.numberOfRides);
                for (int j = 0; j < 3; j++) {
                    Junction thisJunction = mostDangerousJunctions.get(j);
                    if (junction.dangerousnessScore > thisJunction.dangerousnessScore) {
                        Junction tempJunction = mostDangerousJunctions.get(j);
                        mostDangerousJunctions.set(j, junction);
                        if (j < 2) {
                            mostDangerousJunctions.set(j + 1, tempJunction);
                        }
                        break;
                    }

                }
                junctionList.add(junction);
            } else {
                Street street = (Street) segment;
                if (mostDangerousStreetsSouthWest.size() < 3) {
                    mostDangerousStreetsSouthWest.add(street);
                    mostDangerousStreetsSouthWest.add(street);
                    mostDangerousStreetsSouthWest.add(street);
                }
                if (mostDangerousStreetsNorthEast.size() < 3) {
                    mostDangerousStreetsNorthEast.add(street);
                    mostDangerousStreetsNorthEast.add(street);
                    mostDangerousStreetsNorthEast.add(street);
                }
                street.scoreSouthWest = (((cla.getScarinessFactor() * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) /
                        street.numberOfRidesSouthWest)/*/street.seg_length)*10000*/);
                street.scoreNorthEast = (((cla.getScarinessFactor() * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) /
                        street.numberOfRidesNorthEast)/*/street.seg_length)*10000*/);
                street.score = (((cla.getScarinessFactor() * (street.numberOfScaryIncidentsSouthWest + street.numberOfScaryIncidentsNorthEast) +
                        (street.numberOfNonScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsNorthEast)) /
                        (street.numberOfRidesSouthWest + street.numberOfRidesNorthEast))/*/street.seg_length)*10000*/);
                for (int j = 0; j < 3; j++) {
                    Street thisStreetSouthWest = mostDangerousStreetsSouthWest.get(j);
                    if (street.scoreSouthWest > thisStreetSouthWest.scoreSouthWest) {
                        Street tempStreet = mostDangerousStreetsSouthWest.get(j);
                        mostDangerousStreetsSouthWest.set(j, street);
                        if (j < 2) {
                            mostDangerousStreetsSouthWest.set(j + 1, tempStreet);
                        }
                        break;
                    }
                }
                for (int j = 0; j < 3; j++) {
                    Street thisStreetNorthEast = mostDangerousStreetsNorthEast.get(j);
                    if (street.scoreNorthEast > thisStreetNorthEast.scoreNorthEast || (String.valueOf(street.scoreNorthEast)).equals("NaN")) {
                        Street tempStreet = mostDangerousStreetsNorthEast.get(j);
                        mostDangerousStreetsNorthEast.set(j, street);
                        if (j < 2) {
                            mostDangerousStreetsNorthEast.set(j + 1, tempStreet);
                        }
                        break;
                    }
                }
                streetList.add(street);
            }
            added = addSegmentToGeoJson(segment, geoJSONContent, cla);
            if (added && segmentIndex < segmentMap.size() - 1) {
                // add comma and line breaks since there will be more segments
                geoJSONContent.append(",\n\n");
            } else if (added) {
                // only add line breaks since last line
                geoJSONContent.append("\n\n");
            }
            segmentIndex++;
        }
        // remove trailing comma which occurs if there was not a segment added in the end
        if (!added && geoJSONContent.length() > 3) {
            // logger.info(geoJSONContent.substring(geoJSONContent.length()-10));
            geoJSONContent.deleteCharAt(geoJSONContent.length()-3);
        }

        logger.info("Number of Segments: " + segmentMap.size());
        logger.info("Number of Segments with at least 1 ride: " + numberOfSegmentsWithRides);
        logger.info("Number of relevant segments: " + numberOfRelevantSegments);
        writeGeoJSON(geoJSONContent.toString(), cla.getJsonOutputFile());
    }

    private static boolean hasRide(Segment segment) {
        return segment.rides.size() > 0;
    }

    /**
     * Returns true, if a segment was added, and false otherwise (e.g., because it was not relevant).
     */
    private static boolean addSegmentToGeoJson(Segment segment, StringBuilder geoJSONContent,
                                               CommandLineArguments cla) {
        if (!cla.getIgnoreIrrelevantSegments()) {
            geoJSONContent.append(segment.toGeoJson().replaceAll("NaN","-1"));
            numberOfRelevantSegments++;
            return true;
        } else {
            if (isRelevant(segment, cla)) {
                geoJSONContent.append(segment.toGeoJson().replaceAll("NaN","-1"));
                numberOfRelevantSegments++;
                return true;
            }
        }
        return false;
    }

    public static boolean isRelevant(Segment segment, CommandLineArguments cla) {
        if (segment instanceof Junction) {
            Junction junction = (Junction) segment;
            boolean rides = junction.numberOfRides >= cla.getRelevanceThresholdRideCount();
            boolean score = (junction.getScore() >= cla.getRelevanceThresholdScore()) &&
                    (junction.numberOfRides >= cla.getRelevanceThresholdScoreRideCount());

            return rides || score;
        } else {
            Street street = (Street) segment;
            int numRides = street.numberOfRidesSouthWest + street.numberOfRidesNorthEast;
            boolean rides = numRides >= cla.getRelevanceThresholdRideCount();
            boolean score = (street.getScore() >= cla.getRelevanceThresholdScore()) &&
                    (numRides >= cla.getRelevanceThresholdScoreRideCount());

            return rides || score;
        }
    }

}