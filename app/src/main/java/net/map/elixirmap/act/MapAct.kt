package net.map.elixirmap.act

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mapbox.android.core.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import net.map.elixirmap.R
import net.map.elixirmap.util.Categories
import net.map.elixirmap.util.Data.DEFAULT_DISPLACEMENT
import net.map.elixirmap.util.Data.DEFAULT_FASTEST_INTERVAL
import net.map.elixirmap.util.Data.DEFAULT_INTERVAL
import net.map.elixirmap.util.Data.DEFAULT_MAX_WAIT_TIME
import net.map.elixirmap.util.MapUtil.moveCameraTo
import net.map.elixirmap.util.MapUtil.setCurrentLocation

class MapAct : AppCompatActivity(), LocationEngineCallback<LocationEngineResult?> {
    private val imageViews = ArrayList<ImageView?>()
    private val isChoose = intArrayOf(0, 0)
    private var icon_MapboxStreets: ImageView? = null
    private var icon_Outdoors: ImageView? = null
    private var icon_Light: ImageView? = null
    private var icon_Dark: ImageView? = null
    private var icon_Satellite: ImageView? = null
    private var icon_SatelliteStreets: ImageView? = null
    private var icon_TrafficDay: ImageView? = null
    private var icon_TrafficNight: ImageView? = null
    private var MapboxStreets: RelativeLayout? = null
    private var Outdoors: RelativeLayout? = null
    private var Light: RelativeLayout? = null
    private var Dark: RelativeLayout? = null
    private var Satellite: RelativeLayout? = null
    private var SatelliteStreets: RelativeLayout? = null
    private var TrafficDay: RelativeLayout? = null
    private var TrafficNight: RelativeLayout? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var ddd: MapView? = null
    private var ccc: MapboxMap? = null
    private var dd: CameraAnimationsPlugin? = null
    private var menu_map: ImageView? = null
    private var My_position: ImageView? = null
    private var search_ic: ImageView? = null
    private var kk: LocationEngine? = null
    private var lll: LocationEngineRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acti_map)
        initData()
        initOnClickListener()
    }

    private fun initData() {
        My_position = findViewById(R.id.My_position)
        ddd = findViewById(R.id.mapview)
        ccc = ddd!!.getMapboxMap()
        menu_map = findViewById(R.id.menu_map)
        search_ic = findViewById(R.id.search_ic)
        dd = ddd!!.getPlugin(Plugin.MAPBOX_CAMERA_PLUGIN_ID)
        MapSettings()
        setDfMap(ccc!!)
    }

    private fun setDfMap(mapboxMap: MapboxMap) {
        mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        104.065948,
                        30.536823
                    )
                ) //.center(Point.fromLngLat(DEFAULT_LOCATION_LONGITUDE, DEFAULT_LOCATION_LATITUDE))
                .zoom(iii)
                .build()
        )
        mapboxMap.loadStyleUri(Categories.DEFAULT_MAP_STYLE[0],
            Style.OnStyleLoaded { style: Style? -> })
    }

    private fun initOnClickListener() {
        menu_map!!.setOnClickListener { showDialog() }
        My_position!!.setOnClickListener {
            moveCameraTo(
                Point.fromLngLat(longitude, latitude),
                16.0,
                1000,
                dd,
                ccc!!,
                ddd
            )
        }
        search_ic!!.setOnClickListener { startActivity(Intent(this@MapAct, SearchAct::class.java)) }
    }

    private fun showDialog() {
        imageViews.clear()
        val builder = AlertDialog.Builder(this@MapAct)
        val dialog = builder.create()
        var dialogView: View? = null
        dialogView = View.inflate(
            this@MapAct,
            R.layout.dialog_map, null
        )
        dialog.setView(dialogView)
        dialog.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
        dialog.show()
        MapboxStreets = dialogView.findViewById(R.id.MapboxStreets)
        Outdoors = dialogView.findViewById(R.id.Outdoors)
        Light = dialogView.findViewById(R.id.Light)
        Dark = dialogView.findViewById(R.id.Dark)
        Satellite = dialogView.findViewById(R.id.Satellite)
        SatelliteStreets = dialogView.findViewById(R.id.SatelliteStreets)
        TrafficDay = dialogView.findViewById(R.id.TrafficDay)
        TrafficNight = dialogView.findViewById(R.id.TrafficNight)
        icon_MapboxStreets = dialogView.findViewById(R.id.icon_MapboxStreets)
        icon_Outdoors = dialogView.findViewById(R.id.icon_Outdoors)
        icon_Light = dialogView.findViewById(R.id.icon_Light)
        icon_Dark = dialogView.findViewById(R.id.icon_Dark)
        icon_Satellite = dialogView.findViewById(R.id.icon_Satellite)
        icon_SatelliteStreets = dialogView.findViewById(R.id.icon_SatelliteStreets)
        icon_TrafficDay = dialogView.findViewById(R.id.icon_TrafficDay)
        icon_TrafficNight = dialogView.findViewById(R.id.icon_TrafficNight)
        imageViews.add(icon_MapboxStreets)
        imageViews.add(icon_Outdoors)
        imageViews.add(icon_Light)
        imageViews.add(icon_Dark)
        imageViews.add(icon_Satellite)
        imageViews.add(icon_SatelliteStreets)
        imageViews.add(icon_TrafficDay)
        imageViews.add(icon_TrafficNight)
        imageViews[isChoose[0]]!!.visibility = View.GONE
        imageViews[isChoose[1]]!!.visibility = View.VISIBLE
        MapboxStreets!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[0],
                Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 0
            dialog.dismiss()
        })
        Outdoors!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[1], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 1
            dialog.dismiss()
        })
        Light!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[2], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 2
            dialog.dismiss()
        })
        Dark!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[3], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 3
            dialog.dismiss()
        })
        Satellite!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[4], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 4
            dialog.dismiss()
        })
        SatelliteStreets!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[5], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 5
            dialog.dismiss()
        })
        TrafficDay!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[6], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 6
            dialog.dismiss()
        })
        TrafficNight!!.setOnClickListener(View.OnClickListener {
            ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[7], Style.OnStyleLoaded { style: Style? -> })
            isChoose[0] = isChoose[1]
            isChoose[1] = 7
            dialog.dismiss()
        })
    }

    fun MapSettings() {
        val locationPlugin =
            ddd!!.getPlugin<LocationComponentPlugin>(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID)!!
        locationPlugin.updateSettings {->
            this.enabled = true
            this.pulsingEnabled = true // 脉冲效果
            null
        }
        ccc!!.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(104.065948, 30.536823))
                .zoom(iii)
                .build()
        )
        ccc!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[0], Style.OnStyleLoaded { })
        lll = LocationEngineRequest.Builder(DEFAULT_INTERVAL)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setDisplacement(DEFAULT_DISPLACEMENT)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .setFastestInterval(DEFAULT_FASTEST_INTERVAL)
            .build()
    }

    override fun onResume() {
        super.onResume()
        kk = LocationEngineProvider.getBestLocationEngine(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
            return
        }
        kk!!.requestLocationUpdates(lll!!, this, Looper.getMainLooper())
    }

    override fun onPause() {
        super.onPause()
        if (kk != null) {
            kk!!.removeLocationUpdates(this)
        }
    }

    override fun onSuccess(result: LocationEngineResult?) {
        val list = setCurrentLocation(result!!, dd, ccc!!, ddd)
        latitude = list[0]
        longitude = list[1]
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(this, "onFailure : " + exception.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val iii = 8.0 // 初始地图缩放大小
    }
}