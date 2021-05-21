package main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import java.io.File

class CommandLineArguments(parser: ArgParser) {

    val simraRoot by parser
        .storing("-s", "--simraRoot", help = "path to the SimRa dataset root") { File(this) }
        .default(File("./simra_data"))
        .addValidator {
            if (!value.exists()) {
                throw InvalidArgumentException("${value.absolutePath} does not exist")
            }
            if (!value.isDirectory) {
                throw InvalidArgumentException("${value.absolutePath} is not a directory")
            }
        }

    val region by parser
        .storing("-r", "--region", help = "SimRa region to parse")
        .default("UNDEFINED")
        .addValidator {
            require(value != "UNDEFINED") { "You must supply a region with -r " }

            require(simraRoot.listFiles()!!.toList().map { it.nameWithoutExtension }.contains(value)) {
                "SimRa root folder ${simraRoot.absolutePath} does not contain region $value"
            }
        }

    val outputDir by parser
        .storing("-o", "--outputDir", help = "path to directory where to store the output json (overwrites)")
        .default("output_data/")
        .addValidator {
            require(File(value).isDirectory) { "$value is not a directory" }
            require(File(value).exists()) { "$value does not exist" }
        }

    val osmDataDir by parser
        .storing("--osmDir", help = "path to directory in which the by osmPreparation generated files can be found")
        .default("osm_data/")
        .addValidator {
            require(File(value).isDirectory) { "$value is not a directory" }
            require(File(value).exists()) { "$value does not exist" }
        }

    val scarinessFactor by parser
        .storing("--scaryFactor", help = "scaryness factor to increases the weight of scary incidents") {
            this.toDouble()
        }.default(4.4)

    val relevanceThresholdRideCount by parser
        .storing("--minRides", help = "the minimum number of rides for a segment to be included") { this.toInt() }
        .default(50)

    val relevanceThresholdScore by parser
        .storing(
            "--minScore",
            help = "the minimum score for a segment to be included when using --minScoreRides"
        ) { this.toDouble() }
        .default(0.25)

    val relevanceThresholdScoreRideCount by parser
        .storing(
            "--minScoreRides",
            help = "the minimum number of rides for a segment to be included when using --minScore"
        ) { this.toInt() }
        .default(10)

    val ignoreIrrelevantSegments by parser
        .storing(
            "-i",
            "--ignore",
            help = "ignore irrelevant segments defined with --minRides, --minScore, and --minScoreRides "
        ) { this.toBoolean() }
        .default(true)

    val jsonOutputFile = File("$outputDir/$region.json")

    val osmJunctionFile = File(osmDataDir).listFiles()!!.filter { it.name.startsWith("${region}_junctions") }.first()
    val osmSegmentsFile = File(osmDataDir).listFiles()!!.filter { it.name.startsWith("${region}_segments") }.first()
    val osmMetaFile = File(osmDataDir).listFiles()!!.filter { it.name.startsWith("${region}_meta") }.first()

    init {
        require(osmJunctionFile.exists()) { "${osmJunctionFile.absolutePath} does not exist" }
        require(osmSegmentsFile.exists()) { "${osmSegmentsFile.absolutePath} does not exist" }
        require(osmMetaFile.exists()) { "${osmMetaFile.absolutePath} does not exist" }
    }

    override fun toString(): String {
        return "CommandLineArguments(simraRoot=$simraRoot, region='$region', outputDir='$outputDir', osmDataDir='$osmDataDir', scarinessFactor=$scarinessFactor, relevance_threshold_ride_count=$relevanceThresholdRideCount, relevance_threshold_score=$relevanceThresholdScore, relevance_threshold_score_ride_count=$relevanceThresholdScoreRideCount, ignore_irrelevant_segments=$ignoreIrrelevantSegments, jsonOutputFile=$jsonOutputFile, osmJunctionFile=$osmJunctionFile, osmSegmentsFile=$osmSegmentsFile, osmMetaFile=$osmMetaFile)"
    }

    /*****************************************************************
     * Generated methods
     ****************************************************************/


}