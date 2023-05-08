#!/bin/bash

SIMRAROOT=/e/tubCloud/Regions
ALLSUFFIX=-all
OSMDIR=/c/Repo/osmPreparation/output_data
OUTPUTDIR=/c/Repo/osmColoring/output_data
JARDIR=/c/Repo/osmColoring/osmColoring-with-path-last.jar

echo "Generating regions with few rides"
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Alzey --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bruchsal --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bruehl --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Darmstadt --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Friedrichshafen --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Frankfurt --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Kaiserslautern --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Koblenz --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Landau --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Breisgau-Hochschwarzwald --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Saarlouis --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Sigmaringen --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Tuttlingen --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Tübingen --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Vulkaneifel --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Mainz --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Odenwald --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Ortenau --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Pforzheim --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Rastatt --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Saarbrücken --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Trier --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Ulm --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Weimar --minRides 1
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Wetterau --minRides 1


echo "Generating regions with many rides (>400)"
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Augsburg --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Berlin --minRides 50 --minScore 0.25 --minScoreRides 10
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bern --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bielefeld --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Dresden --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Düsseldorf --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Eichwalde --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Freiburg --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Hannover --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Karlsruhe --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Konstanz --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Leipzig --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Mannheim --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r München --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Nuernberg --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Stuttgart --minRides 15 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Ruhrgebiet --minRides 20 --minScore 0.25 --minScoreRides 5
java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Wuppertal --minRides 20 --minScore 0.25 --minScoreRides 5

# echo "Generating regions with many rides showing all"
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Augsburg --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Berlin --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bern --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Bielefeld --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Eichwalde --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Hannover --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r München --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Nuernberg --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Stuttgart --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Ruhrgebiet --minRides 1 --suffix $ALLSUFFIX
# java -jar $JARDIR -s $SIMRAROOT --osmDir $OSMDIR -o $OUTPUTDIR -r Wuppertal --minRides 1 --suffix $ALLSUFFIX
