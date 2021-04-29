/**
 *
 * Copied from the GeoBroker project (https://github.com/MoeweX/geobroker)
 */

package geobroker

import org.apache.logging.log4j.LogManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test

private val logger = LogManager.getLogger()

class LocationTest {

    private var location: Location = Location.random()
    private val n = 100000

    @Before
    fun setUp() {
        location = Location.random()
        Assert.assertNotNull(location)
    }

    @Test
    fun equalsAndRandom() {
        Assert.assertEquals(location, location)
        Assert.assertNotEquals(location, Location.random())
    }

    @Test
    fun testLocationInDistance() {
        val berlin = Location(52.5200, 13.4050)

        val l450m = berlin.locationInDistance(0.450, 45.0)
        logger.info(berlin.distanceKmTo(l450m))
        Assert.assertEquals(0.450, berlin.distanceKmTo(l450m), 0.01)

        val l0 = berlin.locationInDistance(223.4, 0.0)
        Assert.assertEquals(223.4, berlin.distanceKmTo(l0), 0.01)

        val l360 = berlin.locationInDistance(223.4, 360.0)
        Assert.assertEquals(223.4, berlin.distanceKmTo(l360), 0.01)

        val l361 = berlin.locationInDistance(1000.0, 361.0)
        Assert.assertEquals(1000.0, berlin.distanceKmTo(l361), 0.01)

        val ln180 = berlin.locationInDistance(140.1, -180.0)
        Assert.assertEquals(140.1, berlin.distanceKmTo(ln180), 0.01)
    }

    @Test
    fun testDistance() {
        val location = Location(40.0, 40.0)
        val location2 = Location(35.0, 35.0)
        logger.info(location.distanceKmTo(location2))
        Assert.assertTrue(location.distanceKmTo(location2) < 710.000)
        Assert.assertTrue(location.distanceKmTo(location2) > 700.000)
    }

    @Test
    fun testDistanceBerlinHamburg() {
        val berlin = Location(52.5200, 13.4050)
        val hamburg = Location(53.511, 9.9937)
        logger.info(berlin.distanceKmTo(hamburg))
        Assert.assertEquals(253.375, berlin.distanceKmTo(hamburg), 0.5)
    }

}