package Config;

public class Config {
    // center coordinates for Charlottenburg: 52.51616, 13.31339
    // for Bern: 46.946338, 7.443721
    // for Leipzig: 51.3403333,12.37475
    public static final String REGIONCENTERCOORDS ="47.669167, 9.177778";
    // This increases the weight of scary incidents for calculating the dangerousness score
    public static final double SCARINESS_FACTOR = 4.4;

    public static final double MATCH_THRESHOLD = 0.0005;
    // south-west,south-east,north-east,north-west
    // BBOX_LATS for Bern: 47.0238,47.0238,46.86958,46.86958
    // BBOX_LONS for Bern: 7.23381,7.5428,7.5428,7.23381
    // BBOX_LATS for Berlin: 52.3319,52.3319,52.6735,52.6735
    // BBOX_LONS for Berlin: 12.9371,13.8034,13.8034,12.9371
    // BBOX_LATS for Leipzig: 51.223668,51.223668,51.446150,51.446150
    // BBOX_LONS for Leipzig: 12.202565,12.552242,12.552242,12.202565
    // BBOX_LATS for Konstanz: 47.654161,47.654161,47.941796,47.941796
    // BBOX_LONS for Konstanz: 8.608894,9.213842,9.213842,8.608894
    public static final double[] BBOX_LATS = {47.654161,47.654161,47.941796,47.941796};
    public static final double[] BBOX_LONS = {8.608894,9.213842,9.213842,8.608894};


    // Paths to the junctions and segments csv files that contain the extracted osm data
    public static final String JUNCTIONS_PATH = "osm_data/konstanz_junctions.csv";
    public static final String SEGMENTS_PATH = "osm_data/konstanz_streets.csv";

    public static final String GEOJSON_OUTPUT_PATH = "output_data/konstanz.json";

    public static final String DEBUG_PATH = "output_data/debug";

    // Segments with lower number of rides than RELEVANCE_THRESHOLD are not printed if SHOW_SEGMENTS_WITHOUT_DATA is true.
    // Else, they are of IRRELEVANT_COLOR.
    public static final int RELEVANCE_THRESHOLD_RIDECOUNT = 50;
    public static final int RELEVANCE_THRESHOLD_RIDECOUNT_HIGH_SCORE = 10;
    public static final double RELEVANCE_THRESHOLD_SCORE = 0.25;
    public static final boolean SHOW_SEGMENTS_WITHOUT_DATA = true;

    public static final boolean debugOnMap = false;

    public static final int TOPXPERCENT = 10;

    public static final String IRRELEVANT_COLOR = "#808080";
    public static final String SCORE_HIGH_COLOR = "#ff0000";
    public static final String SCORE_MEDIUM_COLOR = "#ff6600";
    public static final String SCORE_LOW_COLOR = "#8080ff";

}
