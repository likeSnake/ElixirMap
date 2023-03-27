package net.map.elixirmap.util

import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.MapAnimationOptions

object MapUtil {
    @JvmStatic
    fun moveCameraTo(
        point: Point?,
        zoom: Double,
        duration: Int,
        cameraAnimationsPlugin: CameraAnimationsPlugin?,
        mapboxMap: MapboxMap,
        mapView: MapView?
    ) {
        if (mapView == null) {
            return
        }
        if (duration != 0 && cameraAnimationsPlugin != null) {
            cameraAnimationsPlugin.flyTo(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(zoom)
                    .build(),
                MapAnimationOptions.Builder().duration(duration.toLong()).build()
            )
        } else {
            mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(zoom)
                    .build()
            )
        }
    }

    @JvmStatic
    fun setCurrentLocation(
        result: LocationEngineResult,
        cameraAnimationsPlugin: CameraAnimationsPlugin?,
        mapboxMap: MapboxMap,
        mapView: MapView?
    ): ArrayList<Double> {
        val lastLocation = result.lastLocation
        val list = ArrayList<Double>()
        if (lastLocation != null) {
            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude
            list.add(latitude)
            list.add(longitude)
            moveCameraTo(
                Point.fromLngLat(longitude, latitude),
                16.0,
                1000,
                cameraAnimationsPlugin,
                mapboxMap,
                mapView
            )
        }
        return list
    }
}