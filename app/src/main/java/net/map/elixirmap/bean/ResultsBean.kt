package net.map.elixirmap.bean

import com.mapbox.geojson.Point


class ResultsBean(name: String, addressInfo: String, arg3: String, point: Point) {
    private  var name: String = name
    private  var address: String = addressInfo
    private  var distance: String  = arg3
    private  var point: Point = point


    fun getPoint(): Point {
        return point
    }

    fun setPoint(point: Point) {
        this.point = point
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getAddress(): String {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }

    fun getDistance(): String {
        return distance
    }

    fun setDistance(distance: String) {
        this.distance = distance
    }
}