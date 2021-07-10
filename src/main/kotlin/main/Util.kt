package main

import org.apache.logging.log4j.LogManager
import org.json.JSONObject
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt


private val logger = LogManager.getLogger()

/**
 * Returns a list of files for the given region.
 * Also adds all rides from the UKNOWN directory, if there are any.
 */
fun getRidesOfRegionAndUNKNOWN(simraRoot: File, region: String, outputDir: String): List<File> {

    val regionFolder = File(simraRoot.absolutePath + File.separator + region + File.separator + "Rides")
    check(regionFolder.exists()) { "Folder for $region does not exist at $regionFolder" }

    /** Read file from String */
    val outputFolder = File(outputDir)

    /** Return entire ride set or only those that were added since last computation depending on whether output is present*/
    val regionRides = if (outputExistent(region, outputFolder)) findNewRides(regionFolder, outputFolder, region, mutableListOf<File>()) else traverseRideDir(regionFolder, mutableListOf<File>())

    check(regionRides.isNotEmpty()) { "There are no ride files in region folder ${regionFolder.absolutePath}" }
    val unknownFolder = File(simraRoot.absolutePath + File.separator + "UNKNOWN" + File.separator + "Rides")

    /** Return entire ride set or only those that were added since last computation depending on whether output is present*/
    val unknownRides = if (unknownFolder.exists()) {
        if (outputExistent(region, outputFolder)) findNewRides(unknownFolder, outputFolder, region, mutableListOf<File>()) else traverseRideDir(unknownFolder, mutableListOf<File>())
    } else {
        logger.warn("UKNOWN folder not found at ${unknownFolder.absolutePath}")
        emptyList<File>()
    }

    return regionRides + unknownRides
}

/**
 * Traverses directory containing ride files, recursively if nested.
 *
 * @param rideDir - the directory to traverse
 * @return rideFiles contained in rideDir
 */
fun traverseRideDir(rideDir: File, rideFiles: MutableList<File>): MutableList<File> {

    rideDir.walk().forEach {
        if (! it.isDirectory) rideFiles.add(it)
    }

    return rideFiles

}

/**
 * Traverses directory containing ride files, recursively if nested.
 * Only add rides that are more recent than timestamp of last computation per meta file.
 *
 * @param rideDir - the directory to traverse
 * @return rideFiles contained in rideDir
 */
fun findNewRides(rideDir: File, outputDir: File, region: String, rideFiles: MutableList<File>): MutableList<File> {

    /** Read timestamp of last computation from meta file */
    val regionMetaFile = outputDir.listFiles()!!.filter { it.name.startsWith("${region}-meta") }.first()
    val jsonO = JSONObject(regionMetaFile.readLines().joinToString(""))

    if (jsonO.has("timeStamp")) {

        val timeStamp = jsonO.getLong("timeStamp")

        /** Don't look in each folder! Only the one corresponding to the respective timestamp & all 'later' ones. */
        /** But support the older model where there was no such file structure. */

        /** Define a date format: e.g. 2021/07 */
        var yearMonthFormat: DateFormat? = SimpleDateFormat("yyyy/MM")

        /** Convert */
        val timeStampMonthYear = Date(timeStamp)

        /** Now only grab new files */
        rideDir.walk().forEach {
            if (! it.isDirectory && (it.lastModified() > timeStamp)) rideFiles.add(it)
        }
        return rideFiles
    } else {
        /** Meta file doesn't contain timestamp, fall back to standard (non-incremental) method */
        return traverseRideDir(rideDir, rideFiles)
    }
}

/**
 * Returns true with the given chance.
 *
 * @param chance - the chance to return true (0 - 100)
 * @return true, if lucky
 */
fun getTrueWithChance(chance: Int): Boolean {
    @Suppress("NAME_SHADOWING") var chance = chance
    // normalize
    if (chance > 100) {
        chance = 100
    } else if (chance < 0) {
        chance = 0
    }
    val random = nextInt(100) + 1 // not 0
    return random <= chance
}

/**
 * Determine if output files already exist for a region.
 * @param region - the region of interest
 * @param outputDir - the location of output files
 * @return true, if geoJSON & meta file already exist for this region
 */

fun outputExistent(region: String, outputDir: File, all: Boolean = false): Boolean {

    val searchString = if (all) "${region}_all" else region

    logger.info(searchString)

    val filesInOutDir = outputDir.listFiles()!!.toList()
            .filter { it.name == "${searchString}.json" || it.name == "${searchString}-meta.json" }

    if (filesInOutDir.size == 2) {
        logger.info("Output data found.")
        return true
    } else {
        logger.info("No output data found.")
        return false
    }
}

fun mergeFiles(region: String, outputDir: File, all: Boolean = false): Unit {

    // iterate over "${region}.json"/"${region}_all.json" &
    // "${region}_newRides.json"/"${region}_all_newRides.json"
    // & combine them

    // then delete "${region}_newRides.json"/"${region}_all_newRides.json"

}

