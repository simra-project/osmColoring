package main

import org.apache.logging.log4j.LogManager
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
 * Converts a timestamp in milliseconds (Long) to a LocalDate.
 */

fun localDateFromMillis(timeStamp: Long): LocalDate {

    return Instant.ofEpochMilli(timeStamp)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
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

