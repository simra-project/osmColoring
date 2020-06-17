package Graph;

import Rides.Incident;
import Rides.Ride;
import Rides.RideBucket;
import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import de.hasenburg.geobroker.server.storage.Raster;

import java.io.File;
import java.util.*;

import static Config.Config.*;
import static Leaflet.LeafletPrinter.*;
import static Rides.Ride.isInBoundingBox;

public class SegmentMapper {

    static List<Junction> mostDangerousJunctions = new ArrayList<>();
    static List<Street> mostDangerousStreetsSouthWest = new ArrayList<>();
    static List<Street> mostDangerousStreetsNorthEast = new ArrayList<>();

    public static void main(String[] args) {
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
        File[] rideFolder = getRidesOfRegion(REGION);
        StringBuilder content = new StringBuilder();
        int numberOfUnmatchedRideBuckets = 0;
        int numberOfAllRides = 0;
        int numberOfIncludedRides = 0;
        int numberOfMatchedIncidents = 0;
        int numberOfUnmatchedIncidents = 0;
        List<Incident> unmatchedIncidents = new ArrayList<>();
        for (int i = 0; i < rideFolder.length; i++) {
            Ride ride = new Ride(rideFolder[i].getPath(),segmentMap,raster);
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

        System.out.println("number of unmatched ride buckets: " + numberOfUnmatchedRideBuckets);
        System.out.println("number of all rides: " + numberOfAllRides);
        System.out.println("number of included rides: " + numberOfIncludedRides);
        System.out.println("number of all incidents: " + (numberOfMatchedIncidents + unmatchedIncidents.size()));
        System.out.println("number of included incidents: " + numberOfMatchedIncidents);
        StringBuilder mapContent = new StringBuilder();

        //int i = 0;
        for (Map.Entry<String,Segment> stringSegmentEntry : segmentMap.entrySet()) {
            Segment segment = stringSegmentEntry.getValue();
            if (segment instanceof Junction) {
                Junction junction = (Junction) segment;
                if (mostDangerousJunctions.size()<3) {
                    mostDangerousJunctions.add(junction);
                    mostDangerousJunctions.add(junction);
                    mostDangerousJunctions.add(junction);
                }
                junction.dangerousnessScore = ((SCARINESS_FACTOR * junction.numberOfScaryIncidents + junction.numberOfNonScaryIncidents) / junction.numberOfRides);
                for (int j = 0; j < 3; j++) {
                    Junction thisJunction = mostDangerousJunctions.get(j);
                    // System.out.println("junction.dangerousnessScore: " + junction.dangerousnessScore + " thisJunction.dangerousnessScore: " + thisJunction.dangerousnessScore);
                    if (junction.dangerousnessScore > thisJunction.dangerousnessScore) {
                        Junction tempJunction = mostDangerousJunctions.get(j);
                        mostDangerousJunctions.set(j, junction);
                        if (j < 2) {
                            mostDangerousJunctions.set(j+1, tempJunction);
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
                if (mostDangerousStreetsSouthWest.size()<3) {
                    mostDangerousStreetsSouthWest.add(street);
                    mostDangerousStreetsSouthWest.add(street);
                    mostDangerousStreetsSouthWest.add(street);
                }
                if (mostDangerousStreetsNorthEast.size()<3) {
                    mostDangerousStreetsNorthEast.add(street);
                    mostDangerousStreetsNorthEast.add(street);
                    mostDangerousStreetsNorthEast.add(street);
                }
                street.scoreSouthWest = (((SCARINESS_FACTOR * street.numberOfScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsSouthWest) / street.numberOfRidesSouthWest)/*/street.seg_length)*10000*/);
                street.scoreNorthEast = (((SCARINESS_FACTOR * street.numberOfScaryIncidentsNorthEast + street.numberOfNonScaryIncidentsNorthEast) / street.numberOfRidesNorthEast)/*/street.seg_length)*10000*/);
                street.score = (((SCARINESS_FACTOR * (street.numberOfScaryIncidentsSouthWest + street.numberOfScaryIncidentsNorthEast) + (street.numberOfNonScaryIncidentsSouthWest + street.numberOfNonScaryIncidentsNorthEast))/(street.numberOfRidesSouthWest + street.numberOfRidesNorthEast))/*/street.seg_length)*10000*/);
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
            content.append(leafletPolygon(segment.poly_vertices_latsArray,segment.poly_vertices_lonsArray));
        }
        writeLeafletHTML(content.toString(), DEBUG_PATH + "\\unmatchedIncidents.html",REGIONCENTERCOORDS);

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
                if (junctionList.get(i).numberOfRides > RELEVANCE_THRESHOLD) {
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
                mapContent.append(junctionList.get(i).toLeaflet());
            } else {
                if (junctionList.get(i).numberOfRides > RELEVANCE_THRESHOLD) {
                    mapContent.append(junctionList.get(i).toLeaflet());
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
                if (streetList.get(i).numberOfRidesSouthWest + streetList.get(i).numberOfRidesNorthEast > RELEVANCE_THRESHOLD) {
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
                mapContent.append(streetList.get(i).toLeaflet(debugOnMap));
            } else {
                if (streetList.get(i).numberOfRidesSouthWest + streetList.get(i).numberOfRidesNorthEast> RELEVANCE_THRESHOLD) {
                    mapContent.append(streetList.get(i).toLeaflet(debugOnMap));
                }
            }
        }
        // System.out.println("streetList.size(): " + streetList.size());

        writeLeafletHTML(mapContent.toString(),OUTPUT_PATH,REGIONCENTERCOORDS);
    }

    // gets a list of ride files from the specified region folder
    private static File[] getRidesOfRegion(String region) {
        File[] regionFolder = new File(PATH + region + "\\Rides").listFiles();
        File[] unknownFolder = new File(PATH + "UNKNOWN\\Rides").listFiles();
        File[] rideFiles = Arrays.copyOf(regionFolder, regionFolder.length + unknownFolder.length);
        System.arraycopy(unknownFolder, 0, rideFiles, regionFolder.length, unknownFolder.length);
        if (rideFiles == null) {
            System.err.println("folder at " + PATH + region + "\\Rides is empty");
            System.exit(-1);
        }
        return rideFiles;
    }
}
