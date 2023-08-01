package Leaflet;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class GeoJsonPrinter {
    long id;
    public static String geoJSONHead() {
        return "{\"type\":\"FeatureCollection\",\"features\":[\n\n";
    }

    public String geoJsonPolygon() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"Feature\",\"id\":\"");
        sb.append(++id);
        sb.append("\",\"properties\":{");

        return sb.toString();
    }

    public static String geoJSONTail() { return "]}";}

    public static void writeGeoJSON(String content, File outputFile) {
        try {
            String json = geoJSONHead() + content + geoJSONTail();
            json = new String(json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
//            JSONObject jsonObject = new JSONObject(json);
//            json = jsonObject.toString(1);

            Files.deleteIfExists(outputFile.toPath());
            Files.write(outputFile.toPath(), json.getBytes(),
                    StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void writeGeoJSONLite(String content, File outputFile) {
        try {
            String json = geoJSONHead() + content + geoJSONTail();
            json = new String(json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
//            JSONObject jsonObject = new JSONObject(json);
//            json = jsonObject.toString(1);

            Files.deleteIfExists(outputFile.toPath());
            Files.write(outputFile.toPath(), json.getBytes(),
                    StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
