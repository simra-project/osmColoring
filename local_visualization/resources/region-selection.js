
let selectMenu = document.getElementById("select-region");

let regions = {
    "Augsburg": "augsburg",
    "Augsburg_all": "augsburg_all",
    "Alzey": "alzey",
    "Alzey_all": "alzey_all",
    "Berlin": "berlin",
    "Berlin10": "berlin10",
    "Breisgau-Hochschwarzwald": "breisgau-hochschwarzwald",
    "Breisgau-Hochschwarzwald_all": "breisgau-hochschwarzwald_all",
    "Bruchsal": "bruchsal",
    "Bruchsal_all": "bruchsal_all",
    "Freiburg": "freiburg",
    "Freiburg_all": "freiburg_all",
    "Hannover": "hannover",
    "Hannover_all": "hannover_all",
    "Hamburg": "hamburg",
    "Hamburg_all": "hamburg_all",
    "Koblenz": "koblenz",
    "Koblenz_all": "koblenz_all",
    "Konstanz": "konstanz",
    "Konstanz_all": "konstanz_all",
    "Landau": "landau",
    "Landau_all": "landau_all",
    "Mainz": "mainz",
    "Mainz_all": "mainz_all",
    "Mannheim": "mannheim",
    "Mannheim_all": "mannheim_all",
    "Nürnberg": "nuernberg",
    "Nürnberg_all": "nuernberg_all",
    "Ortenau": "ortenau",
    "Ortenau_all": "ortenau_all",
    "Rastatt": "rastatt",
    "Rastatt_all": "rastatt_all",
    "Saarlouis": "saarlouis",
    "Saarlouis_all": "saarlouis_all",
    "Stuttgart": "stuttgart",
    "Stuttgart_all": "stuttgart_all",
    "Trier": "trier",
    "Trier_all": "trier_all",
    "Tübingen": "tübingen",
    "Tübingen_all": "tübingen_all",
    "Tuttlingen": "tuttlingen",
    "Tuttlingen_all": "tuttlingen_all",
    "Vulkaneifel": "vulkaneifel",
    "Vulkaneifel_all": "vulkaneifel_all",
    "Wetterau": "wetterau",
    "Wetterau_all": "wetterau_all",
    "Wuppertal": "wuppertal",
    "Wuppertal_all": "wuppertal_all",

}

Object.keys(regions).forEach((key) => {
    let opt = document.createElement("option");
    opt.value = key;
    opt.innerHTML = key;
    selectMenu.appendChild(opt);
})

selectMenu.addEventListener("change", selectOpt);

function selectOpt() {

    let selection = selectMenu.value;
    let para = new URLSearchParams();
    para.append("region", regions[selection]);
    location.href = "map.html?" + para.toString();
}