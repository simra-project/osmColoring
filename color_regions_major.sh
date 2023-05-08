#!/bin/bash

SIMRAROOT=/e/tubCloud/Regions
ALLSUFFIX=-all
OSMDIR=/c/Repo/osmPreparation/output_data
OUTPUTDIR=/

echo "Generating public files"
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Berlin --minRides 50 --minScore 0.25 --minScoreRides 10
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Hannover --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Wuppertal --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Ruhrgebiet --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Bern --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Nuernberg --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r München --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Eichwalde --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Stuttgart --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Augsburg --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Bielefeld --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Konstanz --minRides 15 --minScore 0.25 --minScoreRides 5


echo "Generating files with all rides"
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Berlin --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Hannover --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Wuppertal --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Ruhrgebiet --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Bern --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Nuernberg --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r München --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Eichwalde --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Stuttgart --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Augsburg --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Bielefeld --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-with-path.jar -s $SIMRAROOT --osmDir $OSMDIR -r Konstanz --minRides 1 --suffix $ALLSUFFIX
