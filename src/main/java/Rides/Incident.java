package Rides;

import java.util.HashMap;

public class Incident {
    public double lat, lon;
    public long timestamp;
    public boolean child, trailer, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, scary;
    int pLoc, incident, bike;
    public String description, rideName;

    public Incident( double lat, double lon, long timestamp, int bike, boolean child, boolean trailer, int pLoc, int incident, boolean i1, boolean i2, boolean i3, boolean i4, boolean i5, boolean i6, boolean i7, boolean i8, boolean i9, boolean scary, String description, boolean i10, String rideName) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.bike = bike;
        this.child = child;
        this.trailer = trailer;
        this.pLoc = pLoc;
        this.incident = incident;
        this.i1 = i1;
        this.i2 = i2;
        this.i3 = i3;
        this.i4 = i4;
        this.i5 = i5;
        this.i6 = i6;
        this.i7 = i7;
        this.i8 = i8;
        this.i9 = i9;
        this.scary = scary;
        this.description = description;
        this.i10 = i10;
        this.rideName = rideName;
    }

    public String getParticipants() {
        boolean[] participantBooleans = {i1,i2,i3,i4,i5,i6,i7,i8,i10,i9};
        // String[] participantStrings = {"Bus/Coach","Cyclist","Pedestrian","Delivery Van","Lorry/Truck","Motorcyclist","Car","Taxi/Cab","Electric Scooter","Other"};
        String[] participantStrings = {"Bus","Fahrrad","Fußgänger","Lieferwagen","LKW","Motorrad","PKW","Taxi","E-Scooter","Sonstiges"};

        StringBuilder result = new StringBuilder();
        String p = "";
        for (int i = 0; i < participantBooleans.length; i++) {
            if (participantBooleans[i]) {
                result.append(p);
                p = ",";
                result.append(participantStrings[i]);
            }
        }
        return result.toString();
    }

    public String getIncidentName() {
        //String[] incidentNames = {"Close Pass", "Someone pulling in or out","Near left or right hook","Someone approaching head on","Tailgating","Near-Dooring","Dodging an obstacle (e.g., a dog)","Other"};
        String[] incidentNamesArray = {"Zu dichtes Überholen", "Ein- oder ausparkendes Fahrzeug","Beinahe-Abbiegeunfall","Entgegenkommender Verkehrsteilnehmer","Zu dichtes Auffahren","Beinahe-Dooring","Einem Hindernis ausweichen (z.B. Hund)","Sonstiges"};
        HashMap<String,String> incidentNames = new HashMap<>();
        incidentNames.put("-2",incidentNamesArray[0]);
        incidentNames.put("1",incidentNamesArray[0]);
        incidentNames.put("2",incidentNamesArray[1]);
        incidentNames.put("3",incidentNamesArray[2]);
        incidentNames.put("4",incidentNamesArray[3]);
        incidentNames.put("5",incidentNamesArray[4]);
        incidentNames.put("6",incidentNamesArray[5]);
        incidentNames.put("7",incidentNamesArray[6]);
        incidentNames.put("8",incidentNamesArray[7]);
        return incidentNames.get(String.valueOf(incident));
    }


}
