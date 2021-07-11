import com.fasterxml.jackson.databind.ObjectMapper
import org.geojson.FeatureCollection
import org.json.JSONObject
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class TestDatesAndDirs {

    val outputDir = File("output_data/")

    val region = "Hannover"

    val simraDataDir = File("/Users/theresatratzmuller/Documents/Code.nosync/SimRa/simra_data/${region}/Rides")

    fun localDateFromMillis(timeStamp: Long): LocalDate {

        return Instant.ofEpochMilli(timeStamp)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
    }


    fun findMonthsBetween(): List<String> {

        /** Read timestamp of last computation from meta file */
        val regionMetaFile = outputDir.listFiles()!!.filter { it.name.startsWith("${region}-meta.json") }.first()
        val jsonO = JSONObject(regionMetaFile.readLines().joinToString(""))

        if (jsonO.has("timeStamp")) {

            val timeStamp = jsonO.getLong("timeStamp")

            /** Define a date format: e.g. 2021/07 */
            val yearMonthFormat = DateTimeFormatter.ofPattern("/yyyy/MM")

            /** Convert */
            var timeStampDate = localDateFromMillis(timeStamp)

            /** Print in the given format */

            var timeStampMonthYear = yearMonthFormat!!.format(timeStampDate)

            println(timeStampMonthYear);

            /** Do the same with current date */

            val now = System.currentTimeMillis()

            val nowMonthYear = yearMonthFormat!!.format(localDateFromMillis(now))

            println(nowMonthYear);

            /** Find all the months in between */

            var monthsInBetween = mutableListOf<String>()

            while (timeStampMonthYear != nowMonthYear) {

                monthsInBetween.add(timeStampMonthYear)

                timeStampDate = timeStampDate.plusMonths(1)

                timeStampMonthYear = yearMonthFormat!!.format(timeStampDate)

            }

            monthsInBetween.add(nowMonthYear)

            return monthsInBetween

        }

        return mutableListOf<String>()

    }

    fun getDirs(): List<File> {

        val simraPath = simraDataDir.absolutePath

        var listOfYearMonthFiles = findMonthsBetween().map { File(simraPath.plus(it)) }

        return listOfYearMonthFiles

    }

    @Test
    fun findDataInDirs(): Unit {

        val timeStamp = 1623708000000

        /** Convert */
        var timeStampDate = localDateFromMillis(timeStamp)

        val yMDFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        println(yMDFormat.format(timeStampDate))

        /** Get eligible dirs */
        val dirsToTraverse = getDirs()

        println(dirsToTraverse)

        /** Set up list for storing recent files */

        var rideFiles =  mutableListOf<File>()

        /** Traverse dirs */
        for (dir in dirsToTraverse) {

            /** Now only grab new files */
            dir.walk().forEach {
                println(it.name)
                if (! it.isDirectory && (it.lastModified() > timeStamp)) rideFiles.add(it)
            }

        }

        val mappedRides = rideFiles.map { yMDFormat.format(localDateFromMillis(it.lastModified())) }

        print(mappedRides)

    }

    /**
     * Test reading json from file and merging data
     */
    @Test
    fun mergeFiles(): Unit {

        val outputDir = File("output_data/")

        val region = "Hannover"

        /** Merge them */

        var oldRideData: InputStream? = FileInputStream(outputDir.absolutePath + "/${region}.json")

        var newRideData: InputStream? = FileInputStream(outputDir.absolutePath + "/${region}_newRides.json")

        if (oldRideData == null || newRideData == null) {

            println("Either new or old ride data file couldn't be found!")

            return

        }

        val oldDatafeatureCollection: FeatureCollection = ObjectMapper().readValue(oldRideData, FeatureCollection::class.java)

        val newDatafeatureCollection: FeatureCollection = ObjectMapper().readValue(newRideData, FeatureCollection::class.java)

        for (newFeature in newDatafeatureCollection) {

            val newId = newFeature.id

            for (oldFeature in oldDatafeatureCollection) {

                if (oldFeature.id == newId) {

                    var oldScore = oldFeature.properties["score"]

                    var newScore = newFeature.properties["score"]

                    //oldFeature.properties.replace("score", oldScore, newScore)

                    //println("Old score for id ${newId}: ${oldScore}, new score: ${newScore}, updated: ${oldScore + newScore}")

                }

            }

        }

        // then delete "${region}_newRides.json"/"${region}_all_newRides.json"

    }

}