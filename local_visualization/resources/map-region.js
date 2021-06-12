var params = new URLSearchParams(window.location.search)

var region;
var regionMeta;

switch (params.get("region")) {
    case "augsburg":
        region = "../output_data/Augsburg.json";
        regionMeta = "../output_data/Augsburg-meta.json"
        break;
    case "augsburg_all":
        region = "../output_data/Augsburg_all.json";
        regionMeta = "../output_data/Augsburg_all-meta.json"
        break;
    case "alzey_all":
        region = "../output_data/Alzey_all.json";
        regionMeta = "../output_data/Alzey_all-meta.json"
        break;
    case "berlin":
        region = "../output_data/Berlin.json";
        regionMeta = "../output_data/Berlin-meta.json";
        break;
    case "berlin10":
        region = "../output_data/Berlin10.json";
        regionMeta = "../output_data/Berlin10-meta.json";
        break;
    case "breisgau-hochschwarzwald":
        region = "../output_data/Breisgau-Hochschwarzwald.json";
        regionMeta = "../output_data/Breisgau-Hochschwarzwald-meta.json";
        break;
    case "breisgau-hochschwarzwald_all":
        region = "../output_data/Breisgau-Hochschwarzwald_all.json";
        regionMeta = "../output_data/Breisgau-Hochschwarzwald_all-meta.json";
        break;
    case "bruchsal":
        region = "../output_data/Bruchsal.json";
        regionMeta = "../output_data/Bruchsal-meta.json";
        break;
    case "bruchsal_all":
        region = "../output_data/Bruchsal_all.json";
        regionMeta = "../output_data/Bruchsal_all-meta.json";
        break;
    case "freiburg":
        region = "../output_data/Freiburg.json";
        regionMeta = "../output_data/Freiburg-meta.json";
        break;
    case "Freiburg_all":
        region = "../output_data/Freiburg_all.json";
        regionMeta = "../output_data/Freiburg_all-meta.json";
        break;
    case "hannover":
        region = "../output_data/Hannover.json";
        regionMeta = "../output_data/Hannover-meta.json";
        break;
    case "hannover_all":
        region = "region/Hannover_all.json";
        regionMeta = "region/Hannover_all-meta.json";
        break;
    case "hamburg":
        region = "../output_data/Freiburg.json";
        regionMeta = "../output_data/Freiburg-meta.json";
        break;
    case "hamburg_all":
        region = "../output_data/Hamburg_all.json";
        regionMeta = "../output_data/Hamburg_all-meta.json";
        break;
    case "koblenz":
        region = "../output_data/Koblenz.json";
        regionMeta = "../output_data/Koblenz-meta.json"
        break;
    case "koblenz_all":
        region = "../output_data/Koblenz_all.json";
        regionMeta = "../output_data/Koblenz_all-meta.json"
        break;
    case "konstanz":
        region = "../output_data/Konstanz.json";
        regionMeta = "../output_data/Konstanz-meta.json"
        break;
    case "konstanz_all":
        region = "../output_data/Konstanz_all.json";
        regionMeta = "../output_data/Konstanz_all-meta.json"
        break;
    case "landau":
        region = "../output_data/Landau.json";
        regionMeta = "../output_data/Landau-meta.json"
        break;
    case "landau_all":
        region = "../output_data/Landau_all.json";
        regionMeta = "../output_data/Landau_all-meta.json"
        break;
    case "mainz":
        region = "../output_data/Mainz.json";
        regionMeta = "../output_data/Mainz-meta.json"
        break;
    case "mainz_all":
        region = "../output_data/Mainz_all.json";
        regionMeta = "../output_data/Mainz_all-meta.json"
        break;
    case "mannheim":
        region = "../output_data/Mannheim.json";
        regionMeta = "../output_data/Mannheim-meta.json"
        break;
    case "mannheim_all":
        region = "../output_data/Mannheim_all.json";
        regionMeta = "../output_data/Mannheim_all-meta.json"
        break;
    case "nuernberg":
        region = "../output_data/Nuernberg.json";
        regionMeta = "../output_data/Nuernberg-meta.json"
        break;
    case "nuernberg_all":
        region = "../output_data/Nuernberg_all.json";
        regionMeta = "../output_data/Nuernberg_all-meta.json"
        break;
    case "ortenau":
        region = "../output_data/Ortenau.json";
        regionMeta = "../output_data/Ortenau-meta.json"
        break;
    case "ortenau_all":
        region = "../output_data/Ortenau_all.json";
        regionMeta = "../output_data/Ortenau_all-meta.json"
        break;
    case "rastatt":
        region = "../output_data/Rastatt.json";
        regionMeta = "../output_data/Rastatt-meta.json"
        break;
    case "rastatt_all":
        region = "../output_data/Rastatt_all.json";
        regionMeta = "../output_data/Rastatt_all-meta.json"
        break;
    case "saarlouis":
        region = "../output_data/Saarlouis.json";
        regionMeta = "../output_data/Saarlouis-meta.json"
        break;
    case "saarlouis_all":
        region = "../output_data/Saarlouis_all.json";
        regionMeta = "../output_data/Saarlouis_all-meta.json"
        break;
    case "stuttgart":
        region = "../output_data/Stuttgart.json";
        regionMeta = "../output_data/Stuttgart-meta.json"
        break;
    case "stuttgart_all":
        region = "../output_data/Stuttgart_all.json";
        regionMeta = "../output_data/Stuttgart_all-meta.json"
        break;
    case "trier":
        region = "../output_data/Trier.json";
        regionMeta = "../output_data/Trier-meta.json"
        break;
    case "trier_all":
        region = "../output_data/Trier_all.json";
        regionMeta = "../output_data/Trier_all-meta.json"
        break;
    case "tübingen":
        region = "region/Tübingen.json";
        regionMeta = "region/Tübingen-meta.json"
        break;
    case "tübingen_all":
        region = "region/Tübingen_all.json";
        regionMeta = "region/Tübingen_all-meta.json"
        break;
    case "tuttlingen":
        region = "../output_data/Tuttlingen.json";
        regionMeta = "../output_data/Tuttlingen-meta.json"
        break;
    case "tuttlingen_all":
        region = "../output_data/Tuttlingen_all.json";
        regionMeta = "../output_data/Tuttlingen_all-meta.json"
        break;
    case "vulkaneifel":
        region = "../output_data/Vulkaneifel.json";
        regionMeta = "../output_data/Vulkaneifel-meta.json"
        break;
    case "vulkaneifel_all":
        region = "../output_data/Vulkaneifel_all.json";
        regionMeta = "../output_data/Vulkaneifel_all-meta.json"
        break;
    case "wetterau":
        region = "../output_data/Wetterau.json";
        regionMeta = "../output_data/Wetterau-meta.json"
        break;
    case "wetterau_all":
        region = "../output_data/Wetterau_all.json";
        regionMeta = "../output_data/Wetterau_all-meta.json"
        break;
    case "wuppertal":
        region = "../output_data/Wuppertal.json";
        regionMeta = "../output_data/Wuppertal-meta.json"
        break;
    case "wuppertal_all":
        region = "../output_data/Wuppertal_all.json";
        regionMeta = "../output_data/Wuppertal_all-meta.json"
        break;
    default:
        region = "../output_data/berlin.json";
        regionMeta = "../output_data/berlin-meta.json";
}

var geojson;

try {
    fetch(regionMeta)
        .then(response => response.json())
        .then(data => {
            document.getElementById("regionTitle").innerHTML = data.regionTitle
            document.getElementById("regionDescription").innerHTML = data.regionDescription
            if (data.regionDate != undefined) {
                document.getElementById("regionDate").innerHTML = data.regionDate
            }
            map.setView(data.mapView, data.mapZoom);
        })

    fetch(region)
        .then(response => response.json())
        .then(data => {
            geojson = L.geoJson(data, {
                style: style,
                onEachFeature: onEachFeature,
                filter: withoutIncidents
            }).addTo(polygonGroup);
        })
} catch (error) {
    console.log(error);
}