# SimRa MapJson Generator

This project creates the region json files that are visualized on the [SimRa project website](https://github.com/simra-project/simra-project.github.io/tree/master).

## Requirements

- Git
- Maven
- Java (probably 11 or higher)

## How to use

usage: [-h] [-s SIMRAROOT] [-r REGION] [-o OUTPUTDIR] [--osmDir OSMDIR]
[--scaryFactor SCARYFACTOR] [--minRides MINRIDES] [--minScore MINSCORE]
[--minScoreRides MINSCORERIDES] [-i IGNORE]

optional arguments:
-h, --help                      show this help message and exit

-s SIMRAROOT,                   path to the SimRa dataset root
--simraRoot SIMRAROOT

-r REGION, --region REGION      SimRa region to parse

-o OUTPUTDIR,                   path to directory where to store the output
--outputDir OUTPUTDIR           json (overwrites)

--osmDir OSMDIR                 path to directory in which the by
osmPreparation generated files can be found

--scaryFactor SCARYFACTOR       scaryness factor to increases the weight of
scary incidents

--minRides MINRIDES             the minimum number of rides for a segment to
be included

--minScore MINSCORE             the minimum score for a segment to be
included when using --minScoreRides

--minScoreRides MINSCORERIDES   the minimum number of rides for a segment to
be included when using --minScore

-i IGNORE, --ignore IGNORE      ignore irrelevant segments defined with
--minRides, --minScore, and --minScoreRides


## ToDos

- Rename RasterAPI
