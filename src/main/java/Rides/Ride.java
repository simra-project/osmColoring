package Rides;

import Graph.Segment;
import de.hasenburg.geobroker.server.storage.Raster;

import java.awt.geom.Path2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Ride {

    List<RideBucket> rideBuckets = new ArrayList<>();
    List<Incident> incidents = new ArrayList<>();
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
            RideBucket thisRidebucket = null;
            double towardsSouthWest = 0.0;
            List<RideBucket> rideBucketsOfOneSegment = new ArrayList<>();
            List<Incident> incidentsOfOneSegment = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // change from incident part to ride part
                if (line.startsWith("====")) {
                    incidentPart = false;
                    continue;
                }
                if (incidentPart && !line.endsWith(",,,,,") && line.length()>5) {
                    // System.out.println("incident line: " + line);
                    String[] lineArray = line.split(",", -1);
                    try {
                        incidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], lineArray[20].equals("1")));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        /*
                        System.err.println("i10 missing in incident of ride " + pathToRide);
                        System.err.println("problematic line: " + line);
                        System.err.println("setting i10 as false");
                        */
                        incidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], false));
                    }
                    /*if (line.startsWith("====")) {
                        break;
                    } else if (line.length() > 1 && !line.endsWith(",,,,,,,,,,,,") && !line.contains("#") && ) {
                        String[] lineArray = line.split(",",-1);
                        try {
                            incidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], lineArray[20].equals("1")));
                        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                            System.err.println("i10 missing in incident of ride " + pathToRide);
                            System.err.println("problematic line: " + line);
                            System.err.println("setting i10 as false");
                            incidents.add(new Incident(Double.valueOf(lineArray[1]), Double.valueOf(lineArray[2]), Long.valueOf(lineArray[3]), Integer.valueOf(lineArray[4]), lineArray[5].equals("1"), lineArray[6].equals("1"), Integer.valueOf(lineArray[7]), Integer.valueOf(lineArray[8]), lineArray[9].equals("1"), lineArray[10].equals("1"), lineArray[11].equals("1"), lineArray[12].equals("1"), lineArray[13].equals("1"), lineArray[14].equals("1"), lineArray[15].equals("1"), lineArray[16].equals("1"), lineArray[17].equals("1"), lineArray[18].equals("1"), lineArray[19], false));
                        }
                    }
                    */
                } else if (!incidentPart && !line.contains("#") && !line.startsWith("lat")) {
                    if (!line.startsWith(",,")) {
                        //System.out.println("ride line: " + line);
                        String[] lineArray = line.split(",",-1);
                        thisRidebucket = new RideBucket(Double.valueOf(lineArray[0]),Double.valueOf(lineArray[1]),Long.valueOf(lineArray[5]),segmentMap,raster, (ArrayList<Segment>)visitedSegments);
                        /*
                        if(isInBoundingBox(thisRidebucket.lat, thisRidebucket.lon,new double[]{52.51684217893412,52.51590351270207,52.51584950757138,52.51696424869295,52.517021411905226},new double[]{13.324215996350954,13.32341130266033,13.32358110930224,13.324547987630663,13.324380979980297})) {
                            System.out.println(thisRidebucket.lon + "," + thisRidebucket.lat);
                            System.out.println("13.324215996350954, 52.51684217893412\n" +
                                    "13.32341130266033, 52.51590351270207\n" +
                                    "13.32358110930224, 52.51584950757138\n" +
                                    "13.324547987630663, 52.51696424869295\n" +
                                    "13.324380979980297, 52.517021411905226\n" +
                                    "13.324215996350954, 52.51684217893412");
                            System.out.println(thisRidebucket.segment == null);
                            System.exit(0);
                        }
                        */

                        rideBuckets.add(thisRidebucket);
                        if (thisRidebucket.segment == null) {
                            //System.out.println("lat,lon: " + thisRidebucket.lat + "," + thisRidebucket.lon);
                            continue;
                        }
                        // System.out.println("id: " + thisRidebucket.segment.id + " isJunction: " + thisRidebucket.segment.isJunction);
                        // if we are still in the same segment
                        if (lastRideBucket == null || lastRideBucket.segment == thisRidebucket.segment) {
                            rideBucketsOfOneSegment.add(thisRidebucket);
                            // loop through each Incident of the ride and look, whether it happened in this RideBucket.
                            for (int i = 0; i < incidents.size(); i++) {
                                Incident thisIncident = incidents.get(i);
                                if (thisIncident.lat == thisRidebucket.lat && thisIncident.lon == thisRidebucket.lon) {
                                    incidentsOfOneSegment.add(thisIncident);
                                }
                            }
                            if (lastRideBucket != null) {
                                towardsSouthWest += calculateDirectionToLastRideBucket(lastRideBucket, thisRidebucket);
                            }

                        } else { // entered new Segment!
                            // last segment was a junction, increase number of rides by one and number of incidents
                            if (lastRideBucket.segment.isJunction) {
                                lastRideBucket.segment.numberOfRides++;
                                lastRideBucket.segment.numberOfIncidents += incidentsOfOneSegment.size();
                            } else {
                                // System.out.println(Arrays.toString(rideBucketsOfOneSegment.toArray(new Segment[0])));
                                if (!rideBucketsOfOneSegment.isEmpty()) {
                                    if (towardsSouthWest > 0) {
                                        lastRideBucket.segment.numberOfRidesSouthWest++;
                                        lastRideBucket.segment.numberOfIncidentsSouthWest += incidentsOfOneSegment.size();
                                    } else if (towardsSouthWest < 0) {

                                        lastRideBucket.segment.numberOfRidesNorthEast++;
                                        lastRideBucket.segment.numberOfIncidentsNortheast += incidentsOfOneSegment.size();
                                    }
                                    // reset incidentsOfOneSegment and towardsSouthWest
                                    incidentsOfOneSegment = new ArrayList<>();
                                    towardsSouthWest = 0.0;
                                }
                            }

                        }
                        lastRideBucket = thisRidebucket;
                    }

                }
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

    private static boolean isInBoundingBox(double lat, double lon, double[] polygonLats, double[] polygonLons) {

        Path2D path = new Path2D.Double();

        path.moveTo(polygonLats[0], polygonLons[0]);
        for(int i = 1; i < polygonLats.length; ++i) {
            path.lineTo(polygonLats[i], polygonLons[i]);
        }
        path.lineTo(polygonLats[0], polygonLons[0]);
        //path.closePath();
        return /*true;//*/path.contains(lat,lon);

    }
}
