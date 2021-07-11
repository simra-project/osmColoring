package main

import org.apache.logging.log4j.LogManager
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.time.format.DateTimeFormatter


private val logger = LogManager.getLogger()

/**
 * Find directories containing new data, i.e. rides that were uploaded since osmColoring was executed last
 * for this region (according to timestamp in ${region-meta.json).
 * @param timeStamp - the time at which osmColoring was last run for the region in question as parsed from meta-file
 * @param rideDir - the top level directory in which the ride data is contained
 * @return the directories containing new data, i.e., if osmColoring was last run on 2021-06-14 and it is now
 *          the 11th of July those directories will be
 *          regionDir/2021/06
 *          regionDir/2021/07
 */

fun findDirsWithNewData(timeStamp: Long, rideDir: File): List<File> {

    /** Define a date format: e.g. /2021/07 (slash in front is for concatenation with rideDir) */
    val yearMonthFormat = DateTimeFormatter.ofPattern("/yyyy/MM")

    /** Convert */
    var timeStampDate = localDateFromMillis(timeStamp)

    /** Format */

    var timeStampMonthYear = yearMonthFormat!!.format(timeStampDate)

    println(timeStampMonthYear);

    /** Do the same with current date */

    val now = System.currentTimeMillis()

    val nowMonthYear = yearMonthFormat!!.format(localDateFromMillis(now))

    /** Find all the months in between */

    var monthsInBetween = mutableListOf<String>()

    while (timeStampMonthYear != nowMonthYear) {

        monthsInBetween.add(timeStampMonthYear)

        timeStampDate = timeStampDate.plusMonths(1)

        timeStampMonthYear = yearMonthFormat!!.format(timeStampDate)

    }

    monthsInBetween.add(nowMonthYear)

    /** Map to file & return */

    val simraPath = rideDir.absolutePath

    return monthsInBetween.map { File(simraPath.plus(it)) }

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

        val newDataDirs = findDirsWithNewData(timeStamp, rideDir)

        /** Traverse dirs */
        for (dir in newDataDirs) {
            dir.walk().forEach {
                if (!it.isDirectory && (it.lastModified() > timeStamp)) rideFiles.add(it)
            }
        }
        return rideFiles
    } else {
        /** Meta file doesn't contain timestamp, fall back to standard (non-incremental) method */
        return traverseRideDir(rideDir, rideFiles)
    }
}

fun mergeFiles(region: String, outputDir: File, all: Boolean = false): Unit {

    // iterate over "${region}.json"/"${region}_all.json" &
    // "${region}_newRides.json"/"${region}_all_newRides.json"
    // & combine them

    /** Read new and old data from outputDir*/
    val newRideData = outputDir.listFiles()!!.filter { it.name.startsWith("${region}_newRides.json") }.first()

    val existentRideData = outputDir.listFiles()!!.filter { it.name.startsWith("${region}.json") }.first()

    /** Handle scenario where either of these files is missing (shouldn't happen, but gotta be robust) */
    if (newRideData == null || existentRideData == null) {

        logger.debug("Either new or old ride data file couldn't be retrieved from outputDir!")

        return

    }

    /** Merge them */

    val newDataJsonO = JSONObject(newRideData.readLines().joinToString(""))

    var oldDataJsonO = JSONObject(existentRideData.readLines().joinToString(""))


    // then delete "${region}_newRides.json"/"${region}_all_newRides.json"

}