package Rides;

import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import de.hasenburg.geobroker.server.storage.Raster;

import java.awt.geom.Path2D;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Config.Config.BBOX_LATS;
import static Config.Config.BBOX_LONS;

public class Ride {

    public List<RideBucket> rideBuckets = new ArrayList<>();
    public List<Incident> unmatchedIncidents = new ArrayList<>();
    public int numberOfMatchedIncidents = 0;
    public List<RideBucket> unmatchedRideBuckets = new ArrayList<>();
    HashMap<String, Segment> segmentMap;
    Raster raster;
    List<Segment> visitedSegments = new ArrayList<>();

    public Ride(String pathToRide, HashMap<String, Segment> segmentMap, Raster raster) {

        this.segmentMap = segmentMap;
        this.raster = raster;

        File rideFile = new File(pathToRide);
        try (BufferedReader br = new BufferedReader(new FileReader(rideFile))) {
            // System.out.println(pathToRide.split("\\\\")[7]);
            // Skip file info 57#2
            String line = br.readLine();
            // System.out.println("file info: " + line);
            // Skip header key,lat,lon,ts,bike,childCheckBox,trailerCheckBox,pLoc,incident,i1,i2,i3,i4,i5,i6,i7,i8,i9,scary,desc,i10
            line = br.readLine();
            // System.out.println("header: " + line);
            boolean incidentPart = true;
            RideBucket lastRideBucket = null;
            RideBucket thisRideBucket = null;
            double towardsSouthWest = 0.0;
            List<RideBucket> rideBucketsOfOneSegment = new ArrayList<>();
            ArrayList<Incident> incidentsOfOneSegment = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // change from incident part to ride part
                if (line.startsWith("====")  || line.startsWith("lat") ) {
                    incidentPart = false;
                    continue;
                }
                // add incident
                if (incidentPart) {
                    // System.out.println("incident line: " + line);
                    String[] lineArray = line.split(",", -1);
                    // skip incident if it is "nothing" or corrupted
                    if (line.endsWith(",,,,,") || line.length()<6 || lineArray[8].equals("0")||lineArray[8].equals("")) {
                        continue;
                    }
                    try {
                        unmatchedIncidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], lineArray[20].equals("1"), pathToRide.split("\\\\")[5]));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        unmatchedIncidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], false, pathToRide.split("\\\\")[5]));
                    }
                    // add rideBucket
                } else if (!incidentPart) {
                    // skip line if it does not have GPS, or is a header
                    if (!line.startsWith(",,") && !line.contains("#")  && !line.startsWith("lat")) {
                        //System.out.println("ride line: " + line);
                        String[] lineArray = line.split(",",-1);
                        thisRideBucket = new RideBucket(Double.valueOf(lineArray[0]),Double.valueOf(lineArray[1]),Long.valueOf(lineArray[5]),segmentMap,raster, (ArrayList<Segment>)visitedSegments, pathToRide);
                        if (!thisRideBucket.matchedToSegment && isInBoundingBox(thisRideBucket.lat,thisRideBucket.lon,BBOX_LATS,BBOX_LONS)) {
                            unmatchedRideBuckets.add(thisRideBucket);
                        }
                        rideBuckets.add(thisRideBucket);
                        // skip this RideBucket, if it wasn't matched to a segment
                        if (thisRideBucket.segment == null) {
                            //System.out.println("lat,lon: " + thisRideBucket.lat + "," + thisRideBucket.lon);
                            continue;
                        }
                        // System.out.println("id: " + thisRideBucket.segment.id + " isJunction: " + thisRideBucket.segment.isJunction);
                        // if we are still in the first segment or same segment as before...
                        if (lastRideBucket == null || lastRideBucket.segment == thisRideBucket.segment) {
                            rideBucketsOfOneSegment.add(thisRideBucket);
                            // ...loop through each Incident of the ride and look, whether it happened in this RideBucket.
                            matchIncidentToRideBucket(thisRideBucket, incidentsOfOneSegment);
                            if (lastRideBucket != null) {
                                towardsSouthWest += calculateDirectionToLastRideBucket(lastRideBucket, thisRideBucket);
                            }

                        } else { // entered new Segment!
                            // last segment was a junction, increase number of rides by one and number of incidents
                            if (lastRideBucket.segment instanceof Junction) {
                                ((Junction)lastRideBucket.segment).numberOfRides++;
                                ((Junction)lastRideBucket.segment).numberOfIncidents += incidentsOfOneSegment.size();
                                for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                                    Incident thisIncident = incidentsOfOneSegment.get(i);
                                    if (thisIncident.scary) {
                                        ((Junction)lastRideBucket.segment).scaryIncidentTypes[thisIncident.incident]++;
                                        ((Junction)lastRideBucket.segment).numberOfScaryIncidents++;
                                    } else {
                                        ((Junction)lastRideBucket.segment).nonScaryIncidentTypes[thisIncident.incident]++;
                                        ((Junction)lastRideBucket.segment).numberOfNonScaryIncidents++;
                                    }
                                }
                            } else {// last segment was a street, increase number of rides by one and number of incidents
                                // System.out.println(Arrays.toString(rideBucketsOfOneSegment.toArray(new Segment[0])));
                                if (!rideBucketsOfOneSegment.isEmpty()) {
                                    if (towardsSouthWest > 0) {
                                        ((Street)lastRideBucket.segment).numberOfRidesSouthWest++;
                                        ((Street)lastRideBucket.segment).numberOfIncidentsSouthWest += incidentsOfOneSegment.size();
                                        for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                                            Incident thisIncident = incidentsOfOneSegment.get(i);
                                            if (thisIncident.scary) {
                                                ((Street)lastRideBucket.segment).scaryIncidentTypesSouthWest[thisIncident.incident]++;
                                                ((Street)lastRideBucket.segment).numberOfScaryIncidentsSouthWest++;
                                            } else {
                                                ((Street)lastRideBucket.segment).nonScaryIncidentTypesSouthWest[thisIncident.incident]++;
                                                ((Street)lastRideBucket.segment).numberOfNonScaryIncidentsSouthWest++;
                                            }
                                        }
                                    } else if (towardsSouthWest < 0) {
                                        ((Street)lastRideBucket.segment).numberOfRidesNorthEast++;
                                        ((Street)lastRideBucket.segment).numberOfIncidentsNorthEast += incidentsOfOneSegment.size();
                                        for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                                            Incident thisIncident = incidentsOfOneSegment.get(i);
                                            if (thisIncident.scary) {
                                                ((Street)lastRideBucket.segment).scaryIncidentTypesNorthEast[thisIncident.incident]++;
                                                ((Street)lastRideBucket.segment).numberOfScaryIncidentsNorthEast++;

                                            } else {
                                                ((Street)lastRideBucket.segment).nonScaryIncidentTypesNorthEast[thisIncident.incident]++;
                                                ((Street)lastRideBucket.segment).numberOfNonScaryIncidentsNorthEast++;
                                            }
                                        }
                                    }
                                    // reset incidentsOfOneSegment and towardsSouthWest
                                    incidentsOfOneSegment = new ArrayList<>();
                                    towardsSouthWest = 0.0;
                                }

                            }
                            // and now look, whether an incident happened in this RideBucket
                            matchIncidentToRideBucket(thisRideBucket, incidentsOfOneSegment);
                        }
                        lastRideBucket = thisRideBucket;
                    }

                }
            }
            if (rideBuckets.size()==0) {
                System.out.println("rideBuckets.size()==0: " + pathToRide);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private double calculateDirectionToLastRideBucket(RideBucket lastRideBucket, RideBucket thisRidebucket) {
        double direction = 0.0;
        double towardsSouth = lastRideBucket.lat - thisRidebucket.lat;
        if (towardsSouth > 0) {
            direction += towardsSouth;
        } else if (towardsSouth <= 0) {
            direction -= towardsSouth;
        } else {
            double towardsWest = lastRideBucket.lon - thisRidebucket.lon;
            if (towardsWest > 0) {
                direction += towardsWest;
            } else if (towardsWest < 0) {
                direction -= towardsWest;
            }
        }

        return direction;
    }

    public static boolean isInBoundingBox(double lat, double lon, double[] polygonLats, double[] polygonLons) {

        Path2D path = new Path2D.Double();

        path.moveTo(polygonLats[0], polygonLons[0]);
        for(int i = 1; i < polygonLats.length; ++i) {
            path.lineTo(polygonLats[i], polygonLons[i]);
        }
        path.lineTo(polygonLats[0], polygonLons[0]);
        //path.closePath();
        return /*true;//*/path.contains(lat,lon);

    }

    public ArrayList<RideBucket> getUnmatchedRideBuckets() {
        return (ArrayList<RideBucket>) unmatchedRideBuckets;
    }

    public void matchIncidentToRideBucket(RideBucket thisRideBucket, ArrayList<Incident> incidentsOfOneSegment) {
        for (int i = 0; i < unmatchedIncidents.size(); i++) {
            Incident thisIncident = unmatchedIncidents.get(i);
            if (thisIncident.rideName.contains("VM2_94904613") && thisIncident.timestamp == 1337) {
                System.out.println("thisIncident: " + thisIncident.lat + "," + thisIncident.lon);
                System.out.println("thisRideBucket: " + thisRideBucket.lat + "," + thisRideBucket.lon);
                System.out.println("");
            }
            // if an incident happened here, add it to incidentsOfOneSegment and remove it from the available incidents
            if (thisIncident.lat == thisRideBucket.lat && thisIncident.lon == thisRideBucket.lon) {
                incidentsOfOneSegment.add(thisIncident);
                numberOfMatchedIncidents++;
                unmatchedIncidents.remove(i);
            }
        }
    }
}
