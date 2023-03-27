package net.map.elixirmap.util

import com.mapbox.maps.Style.Companion

object Categories {
    @JvmField
    val DEFAULT_MAP_STYLE = arrayOf<String>(
        Companion.MAPBOX_STREETS,
        Companion.OUTDOORS,
        Companion.SATELLITE,
        Companion.SATELLITE_STREETS,
        Companion.LIGHT,
        Companion.DARK,
        Companion.TRAFFIC_DAY,
        Companion.TRAFFIC_NIGHT
    )
}