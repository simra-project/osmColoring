/**
 *
 * Copied from the GeoBroker project (https://github.com/MoeweX/geobroker)
 */

package geobroker

import geobroker.SpatialContextK.GEO
import org.apache.logging.log4j.LogManager
import org.locationtech.spatial4j.distance.DistanceUtils
import org.locationtech.spatial4j.shape.Point
import kotlin.math.min
import kotlin.random.Random

private val logger = LogManager.getLogger()

/**
 *
 * Copied from the GeoBroker project (https://github.com/MoeweX/geobroker)
 */
class Location(val point: Point) {

    /**
     * Creates a location with the given lat/lon coordinates.
     *
     * @param lat - the latitude (Breitengrad)
     * @param lon - the longitude (Längengrad)
     */
    constructor(lat: Double, lon: Double) : this(GEO.shapeFactory.pointLatLon(lat, lon))

    val lat: Double
        get() = point.lat

    val lon: Double
        get() = point.lon

    /**
     * Distance between this location and the given one, as determined by the Haversine formula, in radians
     *
     * @param toL - the other location
     * @return distance in radians
     */
    fun distanceRadiansTo(toL: Location): Double {
        return GEO.distCalc.distance(point, toL.point)
    }

    /**
     * Distance between this location and the given one, as determined by the Haversine formula, in km
     *
     * @param toL - the other location
     * @return distance in km or -1 if one location is undefined
     */
    fun distanceKmTo(toL: Location): Double {
        return distanceRadiansTo(toL) * DistanceUtils.DEG_TO_KM
    }

    /**
     * @param distance - distance from starting location in km
     * @param direction - direction (0 - 360)
     */
    fun locationInDistance(distance: Double, direction: Double): Location {
        return Location(
            GEO.distCalc.pointOnBearing(
                this.point,
                distance * DistanceUtils.KM_TO_DEG,
                direction,
                GEO,
                GEO.shapeFactory.pointLatLon(0.0, 0.0)
            )
        )
    }

    companion object {
        /**
         * Creates a random location (Not inclusive of (-90, 0))
         */
        @JvmOverloads
        fun random(random: Random = Random.Default): Location {
            // there have been rounding errors
            return Location(
                min((random.nextDouble() * -180.0) + 90.0, 90.0),
                min((random.nextDouble() * -360.0) + 180.0, 180.0)
            )
        }

        fun fromWkt(wkt: String): Location {
            val reader = GEO.formats.wktReader as CustomWKTReader
            return Location(reader.parse(wkt) as Point)
        }

    }

    /*****************************************************************
     * Generated methods
     ****************************************************************/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (point.lat != other.point.lat) return false

        return true
    }

    override fun hashCode(): Int {
        return point.hashCode()
    }

    override fun toString(): String {
        return GEO.formats.wktWriter.toString(point)
    }

}
