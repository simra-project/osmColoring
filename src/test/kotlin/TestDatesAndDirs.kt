import org.json.JSONObject
import org.junit.Test
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class TestDatesAndDirs {

    val outputDir = File("output_data/")

    val simraDataDir = File("/Users/theresatratzmuller/Documents/Code.nosync/SimRa/simra_data")

    val region = "Hannover"

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

        /** Set up list for storing recent files */

        var rideFiles =  mutableListOf<File>()

        /** Traverse dirs */
        for (dir in dirsToTraverse) {

            /** Now only grab new files */
            dir.walk().forEach {
                if (! it.isDirectory && (it.lastModified() > timeStamp)) rideFiles.add(it)
            }

        }

        val mappedRides = rideFiles.map { yMDFormat.format(localDateFromMillis(it.lastModified())) }

        print(mappedRides)

    }

}