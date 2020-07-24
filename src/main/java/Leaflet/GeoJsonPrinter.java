package Leaflet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GeoJsonPrinter {
    long id;
    public static String geoJSONHead() {
        return "var segments = {\"type\":\"FeatureCollection\",\"features\":[";
    }

    public String geoJsonPolygon() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"Feature\",\"id\":\"");
        sb.append(++id);
        sb.append("\",\"properties\":{");

        return sb.toString();
    }

    public static String geoJSONTail() { return "]};";}

    public static void writeGeoJSON(String content, String filePath) {
        try {
            Files.write(Paths.get(filePath), ((geoJSONHead() + content + geoJSONTail())).getBytes(),
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
