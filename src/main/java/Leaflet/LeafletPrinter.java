package Leaflet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class LeafletPrinter {

    public static String leafletPolygon(double[] lats, double[] lons) {
        StringBuilder result = new StringBuilder();
        result.append("\t\tL.polygon([\n");
        for (int i = 0; i < lats.length; i++) {
            result.append("\t\t\t[")
                    .append(lats[i]).append(",")
                    .append(lons[i]).append("],\n");
        }
        result.append("\t\t],).addTo(map);\n");
        return result.toString();
    }

    public static String leafletPolygon(double[] lats, double[] lons, String additionalText) {
        StringBuilder result = new StringBuilder();
        result.append("\t\tL.polygon([\n");
        for (int i = 0; i < lats.length; i++) {
            result.append("\t\t\t[")
                    .append(lats[i]).append(",")
                    .append(lons[i]).append("],\n");
        }
        result.append("\t\t],).addTo(map)")
                .append(".bindPopup(\"")
                .append("<br>").append(additionalText)
                .append("\");\n");
        return result.toString();
    }

    public static String leafletMarker(double lat, double lon, String rideName, long timestamp) {
        StringBuilder result = new StringBuilder();
        result.append("\t\tL.marker([")
                .append(lat).append(",").append(lon).append("]).addTo(map)")
                .append(".bindPopup(\"")
                .append("<br>").append(lat).append(",").append(lon)
                .append("<br>ride: ").append(rideName)
                .append("<br>timestamp: ").append(timestamp)
                .append("\");\n");
        return  result.toString();
    }
    public static String leafletMarker(double lat, double lon, String rideName, long timestamp, String additionalText) {
        StringBuilder result = new StringBuilder();
        result.append("\t\tL.marker([")
                .append(lat).append(",").append(lon).append("]).addTo(map)")
                .append(".bindPopup(\"")
                .append("<br>").append(lat).append(",").append(lon)
                .append("<br>ride: ").append(rideName)
                .append("<br>timestamp: ").append(timestamp)
                .append("<br>").append(additionalText)
                .append("\");\n");
        return  result.toString();
    }

    public static String leafletTail() {
        return "\t\tvar popup = L.popup();\n" +
                "\n" +
                "\tfunction onMapClick(e) {\n" +
                "\t\tpopup\n" +
                "\t\t\t.setLatLng(e.latlng)\n" +
                "\t\t\t.setContent(\"You clicked the map at \" + e.latlng.toString())\n" +
                "\t\t\t.openOn(map);\n" +
                "\t}\n" +
                "\n" +
                "\tmap.on('click', onMapClick);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

}
