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

import static Config.Config.*;
import static Leaflet.GeoJsonPrinter.writeGeoJSON;
import static Leaflet.LeafletPrinter.*;
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
        HashMap<String, Segment> segmentMap = SegmentImporter.importSegments(raster);

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
            Ride ride = new Ride(rideFolder.get(i).getPath(),segmentMap,raster);
            if ( ride.rideBuckets.size() > 0 && isInBoundingBox(ride.rideBuckets.get(0).lat,ride.rideBuckets.get(0).lon,BBOX_LATS,BBOX_LONS)) {
                numberOfIncludedRides++;
                numberOfMatchedIncidents += ride.numberOfMatchedIncidents;
                unmatchedIncidents.addAll(ride.unmatchedIncidents);
                //numberOfUnmatchedIncidents += ride.unmatchedIncidents.size();
            }
            numberOfAllRides ++;
            ArrayList<RideBucket> unmatchedRideBuckets = ride.getUnmatchedRideBuckets();
            numberOfUnmatchedRideBuckets += unmatchedRideBuckets.size();
            /*
            for (int j = 0; j < unmatchedRideBuckets.size(); j++) {
                content.append(leafletMarker(unmatchedRideBuckets.get(j).lat,unmatchedRideBuckets.get(j).lon,unmatchedRideBuckets.get(j).rideName,unmatchedRideBuckets.get(j).timestamp));
            }
             */
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
                junction.dangerousnessScore = ((SCARINESS_FACTOR * junction.numberOfScaryIncidents + junction.numberOfNonScaryIncidents) /
                        junction.numberOfRides);
                for (int j = 0; j < 3; j++) {
                    Junction thisJunction = mostDangerousJunctions.get(j);
                    // System.out.println("junction.dangerousnessScore: " + junction.dangerousnessScore + " thisJunction.dangerousnessScore: " + thisJunction.dangerousnessScore);
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
                /*
                if (SHOW_SEGMENTS_WITHOUT_DATA) {
                    mapContent.append(junction.toLeaflet());
                } else {
                    if (junction.numberOfIncludedRides > RELEVANCE_THRESHOLD) {
                        mapContent.append(junction.toLeaflet());
                    }
                }
                */
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
                street.scoreSouthWest = (((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) /
                        street.numberOfRidesSouthWest)/*/street.seg_length)*10000*/);
                street.scoreNorthEast = (((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) /
                        street.numberOfRidesNorthEast)/*/street.seg_length)*10000*/);
                street.score = (((SCARINESS_FACTOR * (street.numberOfScaryIncidentsSouthWest + street.numberOfScaryIncidentsNorthEast) +
                        (street.numberOfNonScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsNorthEast)) /
                        (street.numberOfRidesSouthWest + street.numberOfRidesNorthEast))/*/street.seg_length)*10000*/);
                for (int j = 0; j < 3; j++) {
                    Street thisStreetSouthWest = mostDangerousStreetsSouthWest.get(j);
                    // System.out.println("street.scoreSouthWest: " + street.scoreSouthWest + " thisStreetSouthWest.scoreSouthWest: " + thisStreetSouthWest.scoreSouthWest);
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
                    // System.out.println("street.scoreNorthEast: " + street.scoreNorthEast + " thisStreetNorthEast.scoreNorthEast: " + thisStreetNorthEast.scoreNorthEast);
                    if (street.scoreNorthEast > thisStreetNorthEast.scoreNorthEast || (String.valueOf(street.scoreNorthEast)).equals("NaN")) {
                        Street tempStreet = mostDangerousStreetsNorthEast.get(j);
                        mostDangerousStreetsNorthEast.set(j, street);
                        if (j < 2) {
                            mostDangerousStreetsNorthEast.set(j + 1, tempStreet);
                        }
                        break;
                    }
                }
                /*
                if (street.id.equals("311894162.0")) {
                    System.out.println("street.id: " + street.id);
                    // ((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest) / street.seg_length*10000
                    System.out.println("street.numberOfScaryIncidentsSouthWest:" + street.numberOfScaryIncidentsSouthWest);
                    System.out.println("street.numberOfNonScaryIncidentsSouthWest: " + street.numberOfNonScaryIncidentsSouthWest);
                    System.out.println("street.numberOfRidesSouthWest: " + street.numberOfRidesSouthWest);
                    System.out.println("(SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest): " + (SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest));
                    System.out.println("street.numberOfRidesSouthWest: " + street.numberOfRidesSouthWest);
                    System.out.println("((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest): " + ((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest));
                    System.out.println("street.seg_length: " + street.seg_length);
                    System.out.println("street.seg_length*10000:" + street.seg_length*10000);
                    System.out.println("((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest) / street.seg_length*10000: " + ((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest) / street.seg_length*10000);

                    System.out.println("street.numberOfScaryIncidentsNorthEast:" + street.numberOfScaryIncidentsNorthEast);
                    System.out.println("street.numberOfNonScaryIncidentsNorthEast: " + street.numberOfNonScaryIncidentsNorthEast);
                    System.out.println("street.numberOfRidesNorthEast: " + street.numberOfRidesNorthEast);
                    System.out.println("(SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast): " + (SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast));
                    System.out.println("street.numberOfRidesNorthEast: " + street.numberOfRidesNorthEast);
                    System.out.println("((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) / street.numberOfRidesNorthEast): " + ((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) / street.numberOfRidesNorthEast));
                    System.out.println("street.seg_length: " + street.seg_length);
                    System.out.println("street.seg_length*10000:" + street.seg_length*10000);
                    System.out.println("((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) / street.numberOfRidesNorthEast) / street.seg_length*10000: " + ((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) / street.numberOfRidesNorthEast) / street.seg_length*10000);

                    System.out.println("street.getScore(): " + street.getScore());
                    System.out.println("street.score: " + street.score);
                }
                 */
                streetList.add(street);
                /*
                if (SHOW_SEGMENTS_WITHOUT_DATA) {
                    mapContent.append(street.toLeaflet());
                } else {
                    if (street.numberOfRidesSouthWest + street.numberOfNonScaryIncidentsNorthEast > RELEVANCE_THRESHOLD) {
                        mapContent.append(street.toLeaflet());
                    }
                }
                */
            }
            // content.append(leafletPolygon(segment.poly_vertices_latsArray,segment.poly_vertices_lonsArray));
            addSegmentToGeoJson(segment, geoJSONContent);
            if (segmentIndex < segmentMap.size() - 1) {
                // add comma and line breaks since there will be more segments
                geoJSONContent.append(",\n\n");
            } else {
                // only add line breaks
                geoJSONContent.append("\n\n");
            }
            segmentIndex++;
        }
        logger.info("Number of Segments: " + segmentMap.size());
        logger.info("Number of Segments with at least 1 ride: " + numberOfSegmentsWithRides);
        logger.info("Number of relevant segments: " + numberOfRelevantSegments);
        // writeLeafletHTML(content.toString(), DEBUG_PATH + "\\unmatchedIncidents.html",REGIONCENTERCOORDS);
        /*
        junctionList.sort(Collections.reverseOrder());
        double junctionsTopXAverageScore = 0.0;
        int junctionsCounted = 0;
        int topJunctionCount = junctionList.size()*(TOPXPERCENT/100);
        for (int i = 0; i < junctionList.size(); i++) {
            if (SHOW_SEGMENTS_WITHOUT_DATA) {
                junctionsTopXAverageScore += junctionList.get(i).getScore();
                junctionsCounted++;
                if (junctionsCounted >= topJunctionCount) {
                    break;
                }
            } else {
                if (junctionList.get(i).numberOfRides >= RELEVANCE_THRESHOLD_RIDECOUNT || junctionList.get(i).getScore() >= RELEVANCE_THRESHOLD_SCORE) {
                    junctionsTopXAverageScore += junctionList.get(i).getScore();
                    junctionsCounted++;
                    if (junctionsCounted >= topJunctionCount) {
                        break;
                    }
                }
            }
        }
        //junctionsTopXAverageScore = junctionsTopXAverageScore / topJunctionCount;
        for (int i = 0; i < junctionList.size(); i++) {
            if (SHOW_SEGMENTS_WITHOUT_DATA) {
                // mapContent.append(junctionList.get(i).toLeaflet().replaceAll("NaN","-1"));
                geoJSONContent.append(junctionList.get(i).toGeoJson().replaceAll("NaN","-1"));
            } else {
                if (isRelevant(junctionList.get(i))) {
                    // mapContent.append(junctionList.get(i).toLeaflet().replaceAll("NaN","-1"));
                    geoJSONContent.append(junctionList.get(i).toGeoJson().replaceAll("NaN","-1"));
                }
            }
        }
        System.out.println("junctionList.size(): " + junctionList.size());
        streetList.sort(Collections.reverseOrder());
        double streetsTopXAverageScore = 0.0;
        int streetsCounted = 0;
        int topStreetCount = streetList.size()/TOPXPERCENT;

        for (int i = 0; i < streetList.size(); i++) {
            if (SHOW_SEGMENTS_WITHOUT_DATA) {
                streetsTopXAverageScore += streetList.get(i).getScore();
                streetsCounted++;
                if (streetsCounted >= topStreetCount) {
                    break;
                }
            } else {
                if (streetList.get(i).numberOfRidesSouthWest + streetList.get(i).numberOfRidesNorthEast >= RELEVANCE_THRESHOLD_RIDECOUNT || streetList.get(i).getScore() >= RELEVANCE_THRESHOLD_SCORE) {
                    streetsTopXAverageScore += streetList.get(i).getScore();
                    streetsCounted++;
                    if (streetsCounted >= topStreetCount) {
                        break;
                    }
                }
            }
        }
        // streetsTopXAverageScore = streetsTopXAverageScore / topStreetCount;

        for (int i = 0; i < streetList.size(); i++) {
            if (SHOW_SEGMENTS_WITHOUT_DATA) {
                // mapContent.append(streetList.get(i).toLeaflet(debugOnMap));
                geoJSONContent.append(streetList.get(i).toGeoJson().replaceAll("NaN","-1"));

            } else {
                if (isRelevant(streetList.get(i))) {
                    // mapContent.append(streetList.get(i).toLeaflet(debugOnMap));
                    geoJSONContent.append(streetList.get(i).toGeoJson().replaceAll("NaN","-1"));
                }
            }
        }
        */
        // System.out.println("streetList.size(): " + streetList.size());
        // writeLeafletHTML(mapContent.toString(), HTML_OUTPUT_PATH,REGIONCENTERCOORDS);
        writeGeoJSON(geoJSONContent.toString(), GEOJSON_OUTPUT_PATH);
    }

    private static boolean hasRide(Segment segment) {
        return segment.rides.size() > 0;
    }

    private static void addSegmentToGeoJson(Segment segment, StringBuilder geoJSONContent) {
        if (SHOW_SEGMENTS_WITHOUT_DATA) {
            geoJSONContent.append(segment.toGeoJson().replaceAll("NaN","-1"));
            numberOfRelevantSegments++;
        } else {
            if (isRelevant(segment)) {
                geoJSONContent.append(segment.toGeoJson().replaceAll("NaN","-1"));
                numberOfRelevantSegments++;
            }
        }
    }

    public static boolean isRelevant(Segment segment) {
        // gelb 50 Fahrten oder 10 Fahrten und mindestens orange
        // nicht jedes davon schwerpunkt
        /*
        if (segment instanceof Junction) {
            Junction junction = (Junction) segment;
            return (junction.numberOfRides >= RELEVANCE_THRESHOLD_RIDECOUNT) || ((junction.getScore() >= RELEVANCE_THRESHOLD_SCORE) && junction.numberOfRides >= RELEVANCE_THRESHOLD_RIDECOUNT_HIGH_SCORE );
        } else {
            Street street = (Street) segment;
            return (street.numberOfRidesSouthWest + street.numberOfRidesNorthEast >= RELEVANCE_THRESHOLD_RIDECOUNT) || ((street.getScore() >= RELEVANCE_THRESHOLD_SCORE) && (street.numberOfRidesSouthWest + street.numberOfRidesNorthEast >= RELEVANCE_THRESHOLD_RIDECOUNT_HIGH_SCORE) );
        }
        */
        if (segment instanceof Junction) {
            Junction junction = (Junction) segment;
            return (junction.numberOfRides >= RELEVANCE_THRESHOLD_RIDECOUNT_HIGH_SCORE );
        } else {
            Street street = (Street) segment;
            return (street.numberOfRidesSouthWest + street.numberOfRidesNorthEast >= RELEVANCE_THRESHOLD_RIDECOUNT_HIGH_SCORE);
        }

    }


}
