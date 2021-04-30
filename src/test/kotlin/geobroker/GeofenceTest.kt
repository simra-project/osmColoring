/**
 *
 * Copied from the GeoBroker project (https://github.com/MoeweX/geobroker)
 */

package geobroker

import org.apache.logging.log4j.LogManager
import org.junit.Assert
import org.junit.Test

private val logger = LogManager.getLogger()

class GeofenceTest {

    @Test
    fun testWKT() {
        val fence = berlinRectangle()
        val wkt = fence.toString()
        logger.info("WKT of berlin rectangle is $fence")
        val fence2 = Geofence.fromWkt(wkt)
        Assert.assertEquals(fence2, fence)
    }

    @Test
    fun testEquals() {
        val fence = berlinRectangle()
        val fence2 = berlinRectangle()
        val fence3 = berlinTriangle()
        Assert.assertEquals(fence, fence2)
        logger.info("Geofences {} and {} equal", fence, fence2)
        Assert.assertNotEquals(fence, fence3)
        logger.info("Geofences {} and {} do not equal", fence, fence3)
    }

    @Test
    fun testContains() {
        val berlin = Location(52.52, 13.405)
        val hamburg = Location(53.511, 9.9937)
        Assert.assertTrue(berlinRectangle().contains(berlin))
        Assert.assertFalse(berlinRectangle().contains(hamburg))
        logger.info("Geofence contains Berlin but not Hamburg")
    }

    @Test
    fun testContainsCircle() {
        val l = Location.random()
        val fence = Geofence.circle(l, 1.9)
        Assert.assertTrue(fence.contains(l))
    }

    @Test
    fun testDisjoint() {
        Assert.assertTrue(berlinRectangle().disjoint(datelineRectangle()))
        Assert.assertFalse(berlinRectangle().disjoint(berlinTriangle()))
        logger.info("Disjoint calculation works properly")
    }

    @Test
    fun centerOfRectangle() {
        var rectangle = Geofence.rectangle(Location(10.0, 10.0), Location(20.0, 20.0))
        Assert.assertEquals(Location(15.0, 15.0), rectangle.center)

        rectangle = Geofence.rectangle(Location(-20.0, -20.0), Location(-10.0, -10.0))
        Assert.assertEquals(Location(-15.0, -15.0), rectangle.center)

        rectangle = Geofence.rectangle(Location(-10.0, -10.0), Location(20.0, 20.0))
        Assert.assertEquals(Location(5.0, 5.0), rectangle.center)
    }

    @Test
    fun testBoundingBoxBerlin() {
        val geofence = berlinRectangle()
        Assert.assertEquals(Location(53.0, 13.0), geofence.boundingBoxNorthWest)
        Assert.assertEquals(Location(53.0, 14.0), geofence.boundingBoxNorthEast)
        Assert.assertEquals(Location(52.0, 14.0), geofence.boundingBoxSouthEast)
        Assert.assertEquals(Location(52.0, 13.0), geofence.boundingBoxSouthWest)
        logger.info("Bounding box calculations work properly for Berlin")
    }

    @Test
    fun testBoundingBoxDateline() {
        val geofence = datelineRectangle()
        Assert.assertEquals(Location(10.0, -10.0), geofence.boundingBoxNorthWest)
        Assert.assertEquals(Location(10.0, 10.0), geofence.boundingBoxNorthEast)
        Assert.assertEquals(Location(-9.0, 10.0), geofence.boundingBoxSouthEast)
        Assert.assertEquals(Location(-9.0, -10.0), geofence.boundingBoxSouthWest)
        logger.info("Bounding box calculations work properly for Dateline")
    }

    private fun datelineRectangle(): Geofence {
        return Geofence.polygon(listOf(
                Location(-9.0, 10.0),
                Location(10.0, 10.0),
                Location(10.0, -10.0),
                Location(-9.0, -10.0)))
    }

    private fun berlinRectangle(): Geofence {
        return Geofence.polygon(listOf(
                Location(53.0, 14.0),
                Location(53.0, 13.0),
                Location(52.0, 13.0),
                Location(52.0, 14.0)))
    }

    private fun berlinTriangle(): Geofence {
        return Geofence.polygon(listOf(
                Location(54.0, 12.0),
                Location(52.0, 15.0),
                Location(50.0, 12.0)))
    }
}