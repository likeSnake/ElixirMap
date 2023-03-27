package net.map.elixirmap.util

object DistanceUtil {
    private const val EARTH_RADIUS = 6378.137
    @JvmStatic
    fun getDistances(
        longitude1: Double, latitude1: Double,
        longitude2: Double, latitude2: Double
    ): Double {
        val Lat1 = rad(latitude1)
        val Lat2 = rad(latitude2)
        val a = Lat1 - Lat2
        val b = rad(longitude1) - rad(longitude2)
        var s = 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(a / 2), 2.0)
                        + (Math.cos(Lat1) * Math.cos(Lat2)
                        * Math.pow(Math.sin(b / 2), 2.0))
            )
        )
        s = s * EARTH_RADIUS
        s = Math.round(s * 10000.0) / 10000.0
        s = s * 1000 //单位：米
        //
        s = Math.round(s / 100.0) / 10.0
        return s
    }

    private fun rad(d: Double): Double {
        return d * Math.PI / 180.0
    }
}