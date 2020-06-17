package Leaflet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static Config.Config.REGIONCENTERCOORDS;

public class LeafletPrinter {
    public static String leafletHead(String centercoords) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Full Screen Leaflet Map</title>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <link rel=\"stylesheet\" href=\"https://d19vzq90twjlae.cloudfront.net/leaflet-0.7/leaflet.css\" />\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "\n" +
                "    <script src=\"https://d19vzq90twjlae.cloudfront.net/leaflet-0.7/leaflet.js\">\n" +
                "    </script>\n" +
                "    <script src=\"TileLayer.Grayscale.js\"></script>\"" +
                "    <script>\n" +
                "        var map = L.map('map').setView([" + REGIONCENTERCOORDS + "], 14);\n" +
                "        mapLink = \n" +
                "            '<a href=\"http://openstreetmap.org\">OpenStreetMap</a>';\n" +
                "        L.tileLayer.grayscale(\n" +
                "            'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; ' + mapLink + ' Contributors',\n" +
                "            maxZoom: 18,\n" +
                "            }).addTo(map);\n" +
                "\t\t\t\n";
    }

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

    public static void writeLeafletHTML(String content, String filePath, String centerCoords) {
        try {
            Files.write(Paths.get(filePath), ((leafletHead(centerCoords) + content + leafletTail())).getBytes(),
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
