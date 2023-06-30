package Rides;

import Segments.Junction;
import Segments.Segment;
import Segments.Street;
import geobroker.Raster;
import main.CommandLineArguments;

import java.awt.geom.Path2D;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class Ride {

    public List<RideBucket> rideBuckets = new ArrayList<>();
    public List<Incident> unmatchedIncidents = new ArrayList<>();
    public List<Incident> matchedIncidents = new ArrayList<>();
    public int numberOfMatchedIncidents = 0;
    private List<RideBucket> unmatchedRideBuckets = new ArrayList<>();
    public boolean isBikeRide = true; // false if the last three speed values' average is >= 50 km/h.
    private Queue<Float> lastThreeSpeeds = new LinkedList<>();
    /**
     * @param lineArray
     * @return Incident with the properties found in lineArray; using placeholder values in case of missing data
     */
    public Incident parseIncident(String[] lineArray, String pathToRide, boolean arrOutOfBounds) {

        double lat = lineArray[1].equals("") ? 0.0 : Double.valueOf(lineArray[1]);

        double lon = lineArray[2].equals("") ? 0.0 : Double.valueOf(lineArray[2]);

        long timestamp = lineArray[3].equals("") ? 0L : Long.valueOf(lineArray[3]);

        int bike = lineArray[4].equals("") ? 0 : Integer.valueOf(lineArray[4]);

        int phoneLocation = lineArray[7].equals("") ? 0 : Integer.valueOf(lineArray[7]);

        int incident = lineArray[8].equals("") ? 0 : Integer.valueOf(lineArray[8]);

        boolean i10 = arrOutOfBounds ? false : lineArray[20].equals("1");

        return new Incident(lat, lon, timestamp, bike, lineArray[5].equals("1"), lineArray[6].equals("1"), phoneLocation, incident, lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], i10, Paths.get(pathToRide).getFileName().toString());

    }

    public Ride(String pathToRide, HashMap<String, Segment> segmentMap, Raster raster, CommandLineArguments cla) {

        File rideFile = new File(pathToRide);
        try (BufferedReader br = new BufferedReader(new FileReader(rideFile))) {
            // System.out.println(pathToRide.split("\\\\")[7]);
            // Skip file info 57#2
            br.readLine();
            // Skip header key,lat,lon,ts,bike,childCheckBox,trailerCheckBox,pLoc,incident,i1,i2,i3,i4,i5,i6,i7,i8,i9,scary,desc,i10
            br.readLine();
            boolean incidentPart = true;
            RideBucket lastRideBucket = null;
            RideBucket thisRideBucket = null;
            double towardsSouthWest = 0.0;
            List<RideBucket> rideBucketsOfOneSegment = new ArrayList<>();
            ArrayList<Incident> incidentsOfOneSegment = new ArrayList<>();
            String next, line;
            next = line = br.readLine();
            for (boolean first = true, last = (line == null); !last; first = false, line = next) {
                last = ((next = br.readLine()) == null);
                // change from incident part to ride part
                if (line.startsWith("====")  || line.startsWith("lat") ) {
                    incidentPart = false;
                    continue;
                }
                // add incident
                if (incidentPart) {
                    String[] lineArray = line.split(",", -1);
                    // skip incident if it is "nothing" or corrupted

                    if (line.endsWith(",,,,,") || line.length()<6 || lineArray[8].equals("0")||lineArray[8].equals("") || lineArray[8].equals("-5")) {
                        continue;
                    }
                    // System.out.println((lineArray[4].length() == 0) + " " + (lineArray[7].length() == 0));
                    if (lineArray[4].length() == 0) {
                        lineArray[4] = "0";
                    }
                    if (lineArray[7].length() == 0) {
                        lineArray[7] = "0";
                    }
                    try {
                        unmatchedIncidents.add(parseIncident(lineArray, pathToRide, false));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        unmatchedIncidents.add(parseIncident(lineArray, pathToRide, true));
                    }
                    // add rideBucket
                } else if (!incidentPart) {
                    // skip line if it does not have GPS, or is a header
                    if (!line.startsWith(",,") && !line.contains("#")  && !line.startsWith("lat")) {
                        String[] lineArray = line.split(",",-1);
                        if(lineArray.length < 7) {
                            continue;
                        }
                        List<Segment> visitedSegments = new ArrayList<>();
                        thisRideBucket = new RideBucket(Double.valueOf(lineArray[0]),Double.valueOf(lineArray[1]),Long.valueOf(lineArray[5]),segmentMap,raster, (ArrayList<Segment>) visitedSegments, pathToRide, this);

                        // Checks if the last three speed readings' average is >= 50 km/h. If yes, this ride is probably
                        // not a bike ride and is flagged to be not included in the osm map
                        if (lastThreeSpeeds.size() < 3) {
                            if (lastRideBucket != null) {
                                lastThreeSpeeds.add(calculateSpeed(lastRideBucket, thisRideBucket));
                            }
                        }
                        if (lastThreeSpeeds.size() == 3) {
                            if (computeAverage(lastThreeSpeeds) >= 50) {
                                isBikeRide = false;
                                break;
                            }
                            lastThreeSpeeds.poll();
                        }

                        if (!thisRideBucket.matchedToSegment && isInBoundingBox(thisRideBucket.lat,thisRideBucket.lon,cla.getBBOX_LATS(),cla.getBBOX_LONS())) {
                            unmatchedRideBuckets.add(thisRideBucket);
                        }
                        rideBuckets.add(thisRideBucket);

                        // skip this RideBucket, if it wasn't matched to a segment
                        if (thisRideBucket.segment == null) {
                            continue;
                        }
                        // if we are still in the first segment or same segment as before...
                        if (lastRideBucket == null || lastRideBucket.segment == thisRideBucket.segment) {
                            rideBucketsOfOneSegment.add(thisRideBucket);
                            if (lastRideBucket != null) {
                                towardsSouthWest += calculateDirectionToLastRideBucket(lastRideBucket, thisRideBucket);
                            }

                            if(last) {
                                updateSegmentStatistics(thisRideBucket,incidentsOfOneSegment,rideBucketsOfOneSegment,towardsSouthWest);
                            }

                        } else { // entered new Segment!
                            // last segment was a junction, update statistics of last segment
                            updateSegmentStatistics(lastRideBucket, incidentsOfOneSegment, rideBucketsOfOneSegment, towardsSouthWest);
                            // reset incidentsOfOneSegment and towardsSouthWest
                            incidentsOfOneSegment = new ArrayList<>();
                            towardsSouthWest = 0.0;
                        }
                        // and now look, whether an incident happened in this RideBucket
                        matchIncidentToRideBucket(thisRideBucket, incidentsOfOneSegment);
                        lastRideBucket = thisRideBucket;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateSegmentStatistics(RideBucket rideBucket, ArrayList<Incident> incidentsOfOneSegment, List<RideBucket> rideBucketsOfOneSegment, double towardsSouthWest) {
        if (rideBucket.segment instanceof Junction) {
            ((Junction)rideBucket.segment).numberOfRides++;
            ((Junction)rideBucket.segment).numberOfIncidents += incidentsOfOneSegment.size();
            for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                Incident thisIncident = incidentsOfOneSegment.get(i);
                rideBucket.segment.incidents.add(thisIncident);
                try {
                    if (thisIncident.scary) {
                        ((Junction)rideBucket.segment).scaryIncidentTypes.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                        ((Junction)rideBucket.segment).numberOfScaryIncidents++;
                    } else {
                        ((Junction)rideBucket.segment).nonScaryIncidentTypes.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                        ((Junction)rideBucket.segment).numberOfNonScaryIncidents++;
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException aioobe) {
                    aioobe.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            if (!rideBucketsOfOneSegment.isEmpty()) {
                if (towardsSouthWest > 0) {
                    ((Street)rideBucket.segment).numberOfRidesSouthWest++;
                    ((Street)rideBucket.segment).numberOfIncidentsSouthWest += incidentsOfOneSegment.size();
                    for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                        Incident thisIncident = incidentsOfOneSegment.get(i);
                        rideBucket.segment.incidents.add(thisIncident);
                        if (thisIncident.scary) {
                            ((Street)rideBucket.segment).scaryIncidentTypesSouthWest.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                            ((Street)rideBucket.segment).numberOfScaryIncidentsSouthWest++;
                        } else {
                            ((Street)rideBucket.segment).nonScaryIncidentTypesSouthWest.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                            ((Street)rideBucket.segment).numberOfNonScaryIncidentsSouthWest++;
                        }
                    }
                } else if (towardsSouthWest < 0) {
                    ((Street)rideBucket.segment).numberOfRidesNorthEast++;
                    ((Street)rideBucket.segment).numberOfIncidentsNorthEast += incidentsOfOneSegment.size();
                    for (int i = 0; i < incidentsOfOneSegment.size(); i++) {
                        Incident thisIncident = incidentsOfOneSegment.get(i);
                        rideBucket.segment.incidents.add(thisIncident);
                        if (thisIncident.scary) {
                            ((Street)rideBucket.segment).scaryIncidentTypesNorthEast.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                            ((Street)rideBucket.segment).numberOfScaryIncidentsNorthEast++;

                        } else {
                            ((Street)rideBucket.segment).nonScaryIncidentTypesNorthEast.merge(String.valueOf(thisIncident.incident),1,Integer::sum);
                            ((Street)rideBucket.segment).numberOfNonScaryIncidentsNorthEast++;
                        }
                    }
                }
            }
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

    public static boolean isInBoundingBox(double lat, double lon, Double[] polygonLats, Double[] polygonLons) {

        Path2D path = new Path2D.Double();

        path.moveTo(polygonLats[0], polygonLons[0]);
        for(int i = 1; i < polygonLats.length; ++i) {
            path.lineTo(polygonLats[i], polygonLons[i]);
        }
        path.lineTo(polygonLats[0], polygonLons[0]);
        return path.contains(lat,lon);

    }

    public ArrayList<RideBucket> getUnmatchedRideBuckets() {
        return (ArrayList<RideBucket>) unmatchedRideBuckets;
    }

    public void matchIncidentToRideBucket(RideBucket thisRideBucket, ArrayList<Incident> incidentsOfOneSegment) {

        for (int i = 0; i < unmatchedIncidents.size(); i++) {
            Incident thisIncident = unmatchedIncidents.get(i);

            // if an incident happened here, add it to incidentsOfOneSegment and remove it from the available incidents
            if (thisIncident.lat == thisRideBucket.lat && thisIncident.lon == thisRideBucket.lon) {

                thisIncident.timestamp = thisRideBucket.timestamp;
                incidentsOfOneSegment.add(thisIncident);
                matchedIncidents.add(thisIncident);
                numberOfMatchedIncidents++;
                unmatchedIncidents.remove(i);
            }
        }
    }
    private float calculateSpeed(RideBucket lastRideBucket, RideBucket thisRideBucket) {
        long lastTS = lastRideBucket.timestamp;
        long thisTS = thisRideBucket.timestamp;
        Location lastLocation = new Location(lastRideBucket.lat,lastRideBucket.lon);
        Location thisLocation = new Location(thisRideBucket.lat,thisRideBucket.lon);
        double distance = lastLocation.distanceTo(thisLocation) / 1000; //distance in km
        double duration = ((double) (thisTS - lastTS))/1000/60/60; // duration in h
        return (float) (distance/duration); // speed in km/h
    }
    private float computeAverage(Collection<Float> myVals) {
        float sum = 0;
        for (float f : myVals) {
            sum += f;
        }
        return sum / myVals.size();
    }
}