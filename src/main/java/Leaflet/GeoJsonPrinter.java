package Leaflet;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GeoJsonPrinter {
    long id;
    public static String geoJSONHead() {
        return "{\"type\":\"FeatureCollection\",\"features\":[";
    }

    public String geoJsonPolygon() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"Feature\",\"id\":\"");
        sb.append(++id);
        sb.append("\",\"properties\":{");

        return sb.toString();
    }

    public static String geoJSONTail() { return "]}";}

    public static void writeGeoJSON(String content, String filePath) {
        try {
            String json = geoJSONHead() + content + geoJSONTail();
//            JSONObject jsonObject = new JSONObject(json);
//            json = jsonObject.toString(1);

            Files.write(Paths.get(filePath), json.getBytes(),
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
