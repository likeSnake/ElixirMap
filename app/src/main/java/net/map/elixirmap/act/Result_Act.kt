package net.map.elixirmap.act

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
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
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.AnnotationType
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import net.map.elixirmap.R
import net.map.elixirmap.util.Categories

class Result_Act() : AppCompatActivity(), View.OnClickListener,
    LocationEngineCallback<LocationEngineResult> {
    private var latitude = 0.0
    private var longitude = 0.0
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var cameraAnimationsPlugin: CameraAnimationsPlugin? = null
    private var menu_map: ImageView? = null
    private var My_position: ImageView? = null
    private var search_ic: ImageView? = null
    private var markLongitude: Double? = null
    private var markLatitude: Double? = null
    private var mLocationEngine: LocationEngine? = null
    private var pointAnnotationManager: PointAnnotationManager? = null
    private var annotationPlugin: AnnotationPlugin? = null
    private var mLocationEngineRequest: LocationEngineRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_search_result)
        markLongitude = intent.getStringExtra("longitude")!!.toDouble()
        markLatitude = intent.getStringExtra("latitude")!!.toDouble()
        My_position = findViewById(R.id.My_position)
        mapView = findViewById(R.id.mapview)
        mapboxMap = mapView!!.getMapboxMap()
        menu_map = findViewById(R.id.menu_map)
        search_ic = findViewById(R.id.search_ic)
        search_ic!!.setOnClickListener(this)
        menu_map!!.setOnClickListener(this)
        My_position!!.setOnClickListener(this)
        cameraAnimationsPlugin = mapView!!.getPlugin(Plugin.MAPBOX_CAMERA_PLUGIN_ID)
        mapboxMap!!.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(104.065948, 30.536823))
                .zoom(DEFAULT_ZOOM_VALUE)
                .build()
        )
        mapboxMap!!.loadStyleUri(Categories.DEFAULT_MAP_STYLE[0],
            Style.OnStyleLoaded { })
        moveCameraTo(Point.fromLngLat(markLongitude!!, markLatitude!!), 16.0, 1000)
        val locationPlugin: LocationComponentPlugin? =
            mapView!!.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID)
        //地图标记插件
        annotationPlugin = mapView!!.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID)
        // 注册点标记管理器
        pointAnnotationManager = annotationPlugin!!.createAnnotationManager(
            AnnotationType.PointAnnotation,
            null
        ) as PointAnnotationManager
        locationPlugin!!.updateSettings {   ->
            this.enabled = true
            this.pulsingEnabled = true // 脉冲效果
            null
        }
        mLocationEngineRequest = LocationEngineRequest.Builder(DEFAULT_INTERVAL)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setDisplacement(DEFAULT_DISPLACEMENT)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .setFastestInterval(DEFAULT_FASTEST_INTERVAL)
            .build()
        addPointAnnotationInMap(markLongitude!!, markLatitude!!)
    }

    /**
     * 在地图中添加Point类型标记
     * @param longitude 添加坐标X
     * @param latitude  添加坐标Y
     */
    fun addPointAnnotationInMap(longitude: Double, latitude: Double) {
        if (annotationPlugin == null) {
            return
        }
        if (pointAnnotationManager == null) {
            pointAnnotationManager = annotationPlugin!!.createAnnotationManager(
                AnnotationType.PointAnnotation,
                null
            ) as PointAnnotationManager
        }
        val res = resources
        val bmp = BitmapFactory.decodeResource(res, R.mipmap.ic_red_marker)
        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(longitude, latitude))
            .withIconImage(bmp)
        pointAnnotationManager!!.create(pointAnnotationOptions)
    }

    override fun onResume() {
        super.onResume()
        mLocationEngine = LocationEngineProvider.getBestLocationEngine(this)
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
        mLocationEngine!!.requestLocationUpdates(
            (mLocationEngineRequest)!!,
            this,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        if (mLocationEngine != null) {
            mLocationEngine!!.removeLocationUpdates(this)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.menu_map -> showPopupMenu(v)
            R.id.My_position -> moveCameraTo(Point.fromLngLat(longitude, latitude), 16.0, 1000)
        }
    }

    //弹出按钮框
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        //menu 布局
        popupMenu.menuInflater.inflate(R.menu.map_type, popupMenu.menu)
        //点击事件
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.map_1 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[0],
                    Style.OnStyleLoaded { })
                R.id.map_2 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[1],
                    Style.OnStyleLoaded { })
                R.id.map_3 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[4],
                    Style.OnStyleLoaded { })
                R.id.map_4 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[5],
                    Style.OnStyleLoaded { style: Style? -> })
                R.id.map_5 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[2],
                    Style.OnStyleLoaded { style: Style? -> })
                R.id.map_6 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[3],
                    Style.OnStyleLoaded { style: Style? -> })
                R.id.map_7 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[6],
                    Style.OnStyleLoaded { style: Style? -> })
                R.id.map_8 -> mapboxMap!!.loadStyleUri(
                    Categories.DEFAULT_MAP_STYLE[7],
                    Style.OnStyleLoaded { style: Style? -> })
            }
            false
        })
        popupMenu.setOnDismissListener { }
        popupMenu.show()
    }

    fun moveCameraTo(point: Point?, zoom: Double, duration: Int) {
        if (mapView == null) {
            return
        }
        if (duration != 0 && cameraAnimationsPlugin != null) {
            cameraAnimationsPlugin!!.flyTo(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(zoom)
                    .build(),
                MapAnimationOptions.Builder().duration(duration.toLong()).build()
            )
        } else {
            mapboxMap!!.setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(zoom)
                    .build()
            )
        }
    }

    //implements LocationEngineCallback<LocationEngineResult> 位置结果回调
    override fun onSuccess(result: LocationEngineResult) {
        val lastLocation = result.lastLocation
        println("位置结果回调")
        if (lastLocation != null) {
            latitude = lastLocation.latitude //维度
            longitude = lastLocation.longitude //经度
            println("$latitude $longitude")
        }
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(this, "onFailure : " + exception.message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val DEFAULT_ZOOM_VALUE = 8.0
        val DEFAULT_DISPLACEMENT = 3.0f
        private val DEFAULT_MAX_WAIT_TIME = 5000L
        private val DEFAULT_FASTEST_INTERVAL = 1000L
        val DEFAULT_INTERVAL = 5000L
    }
}