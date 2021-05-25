#!/bin/bash

SIMRAROOT=./simra_data
ALLSUFFIX=-all

echo "Generating public files"

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Berlin --minRides 50 --minScore 0.25 --minScoreRides 10
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Stuttgart --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Nuernberg --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Konstanz --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Karlsruhe --minRides 15 --minScore 0.25 --minScoreRides 3

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Mannheim --minRides 15 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Freiburg --minRides 15 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Tübingen --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Mainz --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Rastatt --minRides 10 --minScore 0.25 --minScoreRides 3

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Saarlouis --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Landau --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Frankfurt --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Bruchsal --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Breisgau-Hochschwarzwald --minRides 10 --minScore 0.25 --minScoreRides 3

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Alzey --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Koblenz --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Odenwald --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Ortenau --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Vulkaneifel --minRides 10 --minScore 0.25 --minScoreRides 3

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Wetterau --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Trier --minRides 10 --minScore 0.25 --minScoreRides 3
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Tuttlingen --minRides 10 --minScore 0.25 --minScoreRides 3

echo "Generating files with all rides"

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Berlin --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Stuttgart --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Nuernberg --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Konstanz --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Karlsruhe --minRides 1 --suffix $ALLSUFFIX

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Mannheim --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Freiburg --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Tübingen --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Mainz --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Rastatt --minRides 1 --suffix $ALLSUFFIX

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Saarlouis --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Landau --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Frankfurt --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Bruchsal --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Breisgau-Hochschwarzwald --minRides 1 --suffix $ALLSUFFIX

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Alzey --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Koblenz --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Odenwald --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Ortenau --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Vulkaneifel --minRides 1 --suffix $ALLSUFFIX

java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Wetterau --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Trier --minRides 1 --suffix $ALLSUFFIX
java -jar osmColoring-jar-with-dependencies.jar -s $SIMRAROOT -r Tuttlingen --minRides 1 --suffix $ALLSUFFIX

