package main

import Config.Config
import java.io.File
import java.util.*
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

/**
 * Returns a list of files for the given region.
 * Also adds all rides from the UKNOWN directory, if there are any.
 */
fun getRidesOfRegionAndUNKNOWN(simraRoot: File, region: String): List<File> {
    val regionFolder = File(simraRoot.absolutePath + File.separator + region + File.separator + "Rides")
    check(regionFolder.exists()) { "Folder for $region does not exist at $regionFolder" }
    val regionRides = regionFolder.listFiles()!!.toList()
    check(regionRides.isNotEmpty()) { "There are no ride files in region folder ${regionFolder.absolutePath}" }

    val unknownFolder = File(simraRoot.absolutePath + File.separator + "UNKNOWN" + File.separator + "Rides")
    val unknownRides = if (unknownFolder.exists()) {
        unknownFolder.listFiles()!!.toList()
    } else {
        logger.warn("UKNOWN folder not found at ${unknownFolder.absolutePath}")
        emptyList<File>()
    }

    return regionRides + unknownRides
}