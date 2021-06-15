# SimRa MapJson Generator

This project creates the region json files that are visualized on the [SimRa project website](https://github.com/simra-project/simra-project.github.io/tree/master).
In essence, data on incidents in a SimRa region gathered by users of the SimRa app is mapped onto specific areas of the respective region, producing data to be displayed on a Leaflet map.
Depending on their dangerousness score, junctions and street segments appear in green, yellow, orange or red.
The dangerousness score describes the fraction of incidents relative to the number of total rides associated with the respective
junction/segment, with incidents marked as **scary** weighing 4.4 times as much as regular incidents (4.4 is just the default value; the parameter can be tweaked as desired, see below for a detailed description of the parameters and their default values). 

## Required toolchain

- Git
- Maven
- Java (probably 11 or higher)

## Required data

For a specific SimRa region, you will need:
- incident data (obtained from https://github.com/simra-project/dataset), only Berlin thus far, but other data upon request (insert link)
- three files written by [osmPreparation](https://github.com/simra-project/osmPreparation): `{region}_segments_complete_YYY-MM-DD.csv`,  `{region}_junctions_complete_YYY-MM-DD.csv`, as well as `{region}_meta.json`.

## How to use

The entry point for running this project is `src/kotlin/main/Main.kt`. 

A number of input parameters can be specified, with defaults being provided for each (except `r`, region). Therefore, if the defaults are to be accepted only `r` (region) needs to be specified.
- `-s`/`--simraRoot`: the directory from which the incident data will be read. `osmColoring/osm_data` by default.
- `-r`/`--region`: the region the computations shall be carried out for.
- `-o`/`--outputDir: the directory into which output data will be written, i.e. `{region}.json` (the to-be-visualized data) as well as files containing meta-information (`{region}-meta.json`, `{region_all-meta.json}`) such as utilized cut-off values for input parameters listed below.
Default: `osmColoring/output_data`.
- `--osmDir`: the directory from which the data written by `osmPreparation` will be read. Default: `osmColoring/osm_data`.
- `--scaryFactor`: the scariness factor is used as a multiplier for incidents that were marked as `scary`. The default value is 4.4. This means that the weight of a `scary` incident will correspond to 4.4 times the weight of a non-`scary` incident.
- `--minRides`: the minimum number of rides that need to be associated with a segment or junction for it to be included. Default is 50. **Please note: when `minRides` is set to 1, the name of the output files will differ. Please refer to the section on output files for more information.**
- `--minScore`, `--minScoreRides`: as described above, a junction/segment needs to have at least `minRides` associated with it to be included. Using `minScore` and `minScoreRides`, this requirement can be overriden: junctions/segments with a dangerousness
score of at least `minScore` only need to exhibit at least `minScoreRides` to be included. 
- `-i`/`--ignore` (boolean): ignore irrelevant segments defined with `minRides`, `minScore`, and `minScoreRides`. Default is `true`.
- `-h`/`--help`: print help message and exit.

Below is an example run configuration in IntelliJ IDEA, using the default values for the three I/O directories (`simraroot`, `osmDir`, `outputdir`).

![Example run configuration for `Main.kt` in IntelliJ IDEA, using the defaults for data I/O directories.](run_config.png)

Output data (written to `outputDir`):
- `{region}.json`: the GeoJson file containing a region's junctions and street segments as geometric shapes as well as information on their dangerousness; to be plotted on a Leaflet-map.
- `{region}-meta.json`: as the title suggests, this file contains meta information such as the date, centroid and zoom level for the to-be-generated map, and a short description.
- Please note that when `minRides` is set to 1 (i.e., all junctions and street segments with at least one ride associated with them will be included), the names of the two output files will be `{region}_all.json` and `{region}_all-meta.json`. 

## Viewing the result on a map

This project contains the [SimRa project website](https://simra-project.github.io/) as a submodule for the purpose of visualizing the resultant GeoJson data on a map.
Analogous to the [map of Berlin results already published](https://simra-project.github.io/map.html?region=berlin), users can view the results for any region whose data they have access to.

This is how you can use the project website's code to plot your results:
- The SimRa project website is embedded in this project as a sub-module (perhaps you've already noticed the empty `simra-project.github.io`directory). In order to activate
the submodule, execute the following commands:
```
git submodule init
git submodule update
```
- Move the files you've generated using this project (`{region}.json`/`{region}_all.json` and `{region}-meta.json`/`{region}_all-meta.json`) into `simra-project.github.io/region`.
- In `simra-project.github.io/resources/map-region.js`, add the respective region to the `switch (params.get("region"))`-block. Example:
```
case "augsburg":
        region = "region/Augsburg.json";
        regionMeta = "region/Augsburg-meta.json";
        break;
``` 
- Start a local HTTP server (inside the top level directory or the `simra-project.github.io` subdirectory, doesn't really matter): `python3 -m http.server`. In your browser, enter `localhost:8000`. 
The directory structure will appear. If you're not already inside it, navigate into the directory `simra-project.github.io`. Select `map.html`.
In the browser address bar, add the respective URL search parameter for the region in question. Example: 
```
http://localhost:8000/map.html?region=augsburg
```
- That's it! You should be able to view your results on a map. 

## ToDos

- Rename RasterAPI
