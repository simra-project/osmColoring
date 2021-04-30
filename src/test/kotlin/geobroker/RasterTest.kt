/**
 *
 * Copied from the GeoBroker project (https://github.com/MoeweX/geobroker)
 */

package geobroker
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.logging.log4j.LogManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.locationtech.spatial4j.exception.InvalidShapeException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

private val logger = LogManager.getLogger()

@Suppress("PrivatePropertyName")
class RasterTest {

    @Test(expected = InvalidShapeException::class)
    fun testCalculateIndexGranularity1() {
        val raster = Raster(1)
        var calculatedIndex = raster.calculateIndexLocation(Location(10.0, -10.0))

        // even
        assertEquals(Location(10.0, -10.0), calculatedIndex)

        // many fractions
        calculatedIndex = raster.calculateIndexLocation(Location(10.198, -11.198))
        assertEquals(Location(10.0, -12.0), calculatedIndex)

        // exact boundary
        calculatedIndex = raster.calculateIndexLocation(Location(90.0, -180.0))
        assertEquals(Location(90.0, -180.0), calculatedIndex)

        // out of bounds, expect throw
        raster.calculateIndexLocation(Location(91.0, -181.0))
    }

    @Test(expected = InvalidShapeException::class)
    fun testCalculateIndexGranularity10() {

        val raster = Raster(10)
        var calculatedIndex = raster.calculateIndexLocation(Location(10.0, -10.0))

        // even
        assertEquals(Location(10.0, -10.0), calculatedIndex)

        // many fractions
        calculatedIndex = raster.calculateIndexLocation(Location(10.198, -11.198))
        assertEquals(Location(10.1, -11.2), calculatedIndex)

        // exact boundary
        calculatedIndex = raster.calculateIndexLocation(Location(90.0, -180.0))
        assertEquals(Location(90.0, -180.0), calculatedIndex)

        // out of bounds, expect throw
        raster.calculateIndexLocation(Location(91.0, -181.0))
    }

    @Test(expected = InvalidShapeException::class)
    fun testCalculateIndexGranularity100() {

        val raster = Raster(100)
        var calculatedIndex = raster.calculateIndexLocation(Location(10.0, -10.0))

        // even
        assertEquals(Location(10.0, -10.0), calculatedIndex)

        // many fractions
        calculatedIndex = raster.calculateIndexLocation(Location(10.198, -11.198))
        assertEquals(Location(10.19, -11.2), calculatedIndex)

        // exact boundary
        calculatedIndex = raster.calculateIndexLocation(Location(90.0, -180.0))
        assertEquals(Location(90.0, -180.0), calculatedIndex)

        // out of bounds, expect throw
        raster.calculateIndexLocation(Location(91.0, -181.0))
    }

    @Test
    fun testCalculateIndexLocationsForGeofenceRectangle() {
        val raster = Raster(1)
        val fence = Geofence.polygon(Arrays.asList(Location(-0.5, -0.5),
                Location(-0.5, 1.5),
                Location(1.5, 1.5),
                Location(1.5, -0.5)))

        val result = raster.calculateIndexLocations(fence)
        assertEquals(9, result.size.toLong())
        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
        assertTrue(containsLocation(result, Location(-1.0, 1.0)))
        assertTrue(containsLocation(result, Location(0.0, -1.0)))
        assertTrue(containsLocation(result, Location(0.0, 0.0)))
        assertTrue(containsLocation(result, Location(0.0, 1.0)))
        assertTrue(containsLocation(result, Location(1.0, -1.0)))
        assertTrue(containsLocation(result, Location(1.0, 0.0)))
        assertTrue(containsLocation(result, Location(1.0, 1.0)))
    }

    @Test
    fun testCalculateIndexLocationsForGeofenceTriangle() {
        val raster = Raster(1)
        val fence = Geofence.polygon(Arrays.asList(Location(-0.5, -1.5), Location(-0.5, 0.7), Location(1.7, -1.5)))

        val result = raster.calculateIndexLocations(fence)
        assertEquals(8, result.size.toLong())
        assertTrue(containsLocation(result, Location(-1.0, -2.0)))
        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
        assertTrue(containsLocation(result, Location(0.0, -2.0)))
        assertTrue(containsLocation(result, Location(0.0, -1.0)))
        assertTrue(containsLocation(result, Location(0.0, 0.0)))
        assertTrue(containsLocation(result, Location(1.0, -2.0)))
        assertTrue(containsLocation(result, Location(1.0, -1.0)))
    }

    @Test
    fun testCalculateIndexLocationsForGeofenceCircle() {
        val raster = Raster(1)
        val fence = Geofence.circle(Location(0.5, 0.0), 1.1)

        val result = raster.calculateIndexLocations(fence)
        assertEquals(8, result!!.size.toLong())
        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
        assertTrue(containsLocation(result, Location(0.0, -2.0)))
        assertTrue(containsLocation(result, Location(0.0, -1.0)))
        assertTrue(containsLocation(result, Location(0.0, 0.0)))
        assertTrue(containsLocation(result, Location(0.0, 1.0)))
        assertTrue(containsLocation(result, Location(1.0, -1.0)))
        assertTrue(containsLocation(result, Location(1.0, 0.0)))
    }

    @Test
    fun testCalculateIndexLocationsForGeofenceCircle2() {
        val raster = Raster(5)
        val l = Location(39.984702, 116.318417)
        val fence = Geofence.circle(l, 0.1)

        val result = raster.calculateIndexLocations(fence)
        assertTrue(containsLocation(result!!, Location(39.8, 116.2)))
        assertTrue(containsLocation(result, Location(39.8, 116.4)))
        assertTrue(containsLocation(result, Location(40.0, 116.2)))
        assertTrue(containsLocation(result, Location(40.0, 116.4)))
        assertEquals(4, result.size.toLong())
    }

    @Test
    fun testCalculateIndexLocationsForGeofenceCircle3() {
        val raster = Raster(10)
        val l = Location(39.984702, 116.318417)
        val fence = Geofence.circle(l, 0.1)

        val result = raster.calculateIndexLocations(fence)
        assertTrue(containsLocation(result!!, Location(39.8, 116.2)))
        assertTrue(containsLocation(result, Location(39.8, 116.3)))
        assertTrue(containsLocation(result, Location(39.9, 116.2)))
        assertTrue(containsLocation(result, Location(39.9, 116.3)))
        assertTrue(containsLocation(result, Location(39.9, 116.4)))
        assertTrue(containsLocation(result, Location(40.0, 116.2)))
        assertTrue(containsLocation(result, Location(40.0, 116.3)))
        assertTrue(containsLocation(result, Location(40.0, 116.4)))
        assertEquals(8, result.size.toLong())
    }

    @Test
    fun testPutAndThenGet() {
        val raster = Raster(25)
        val l = Location(40.007499, 116.320013)
        val fence = Geofence.circle(l, 0.01)
        val sid = ImmutablePair("test", "1")

        val result = raster.calculateIndexLocations(fence)
        val index = raster.calculateIndexLocation(l)
        assertTrue(containsLocation(result, index))

        raster.putSubscriptionIdIntoRasterEntries(fence, sid)
        val ids = raster.getSubscriptionIdsInRasterEntryForPublisherLocation(l)
        logger.info(ids)
    }

    private fun containsLocation(result: List<RasterEntry>, l: Location?): Boolean {
        for (rasterEntry in result) {
            if (rasterEntry.index == l) {
                return true
            }
        }

        return false
    }

}