package com.example.testlag

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testlag.databinding.ActivityMainBinding
import com.example.testlag.databinding.ActivitySecondBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError

class SecondActivity : AppCompatActivity() {
    private lateinit var map: Map

    // Листенер для обработки нажатий на карту
    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            routePoints = routePoints + point
        }
    }

    // Листенер для обработки результатов построения маршрута
    private val drivingRouteListener = object : DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            routes = drivingRoutes
        }

        override fun onDrivingRoutesError(error: Error) {
            when (error) {
                is NetworkError -> showToast("Routes request error due network issues")
                else -> showToast("Routes request unknown error")
            }
        }
    }

    private var routePoints = emptyList<Point>()
        set(value) {
            field = value
            onRoutePointsUpdated()
        }

    private var routes = emptyList<DrivingRoute>()
        set(value) {
            field = value
            onRoutesUpdated()
        }

    private lateinit var drivingRouter: DrivingRouter
    private var drivingSession: DrivingSession? = null
    private lateinit var placemarksCollection: MapObjectCollection
    private lateinit var routesCollection: MapObjectCollection

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: ActivitySecondBinding
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация карты и добавление слушателя для обработки нажатий
        map = binding.mapView.mapWindow.map
        map.addInputListener(inputListener)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        placemarksCollection = map.mapObjects.addCollection()
        routesCollection = map.mapObjects.addCollection()

        // Инициализация роутера для построения маршрутов
        drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

        getLastLocation()
    }

    private fun getLastLocation() {
        // Проверка наличия разрешений на получение местоположения
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        // Получение последнего известного местоположения
        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { loc ->
            if (loc != null) {
                latitude = loc.latitude
                longitude = loc.longitude
                // Установка точек маршрута: текущего местоположения и местоположения офиса компании

                routePoints = listOf(
                    Point(latitude, longitude),
                    DEFAULT_POINTS_COMPANY,
                )
                // Перемещение камеры карты к текущему местоположению
                val map = binding.mapView.mapWindow.map
                map.move(CameraPosition(Point(latitude, longitude), 13.0f, 0f, 0f))
            }
        }
    }

    private fun requestPermission() {
        // Запрос разрешений на доступ к местоположению
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            1
        )
    }

    override fun onStart() {
        super.onStart()
        // Запуск MapKit
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        // Остановка MapKit
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun onRoutePointsUpdated() {
        placemarksCollection.clear()

        // Если нет точек маршрута, отменяем сессию построения маршрута
        if (routePoints.isEmpty()) {
            drivingSession?.cancel()
            routes = emptyList()
            return
        }

        // Добавление маркеров на карту
        val imageProvider = ImageProvider.fromResource(this, R.drawable.ic_pin)
        routePoints.forEach {
            placemarksCollection.addPlacemark().apply {
                geometry = it
                setIcon(imageProvider, IconStyle().apply {
                    scale = 0.5f
                    zIndex = 20f
                })
            }
        }
        // Если меньше двух точек, не строим маршрут
        if (routePoints.size < 2) return

        // Формирование списка точек запроса для построения маршрута
        val requestPoints = buildList {
            add(RequestPoint(routePoints.first(), RequestPointType.WAYPOINT, null, null))
            addAll(
                routePoints.subList(1, routePoints.size - 1)
                    .map { RequestPoint(it, RequestPointType.VIAPOINT, null, null) })
            add(RequestPoint(routePoints.last(), RequestPointType.WAYPOINT, null, null))
        }

        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        // Запрос построения маршрута
        drivingSession = drivingRouter.requestRoutes(
            requestPoints,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener,
        )
    }

    private fun onRoutesUpdated() {
        // Очистка коллекции маршрутов
        routesCollection.clear()

        if (routes.isEmpty()) return

        // Добавление маршрутов на карту
        routes.forEachIndexed { index, route ->
            routesCollection.addPolyline(route.geometry).apply {
                if (index == 0) styleMainRoute() else styleAlternativeRoute()
            }
        }
    }

    // Стиль для основного маршрута
    private fun PolylineMapObject.styleMainRoute() {
        zIndex = 10f
        setStrokeColor(ContextCompat.getColor(this@SecondActivity, CommonColors.gray))
        strokeWidth = 5f
        outlineColor = ContextCompat.getColor(this@SecondActivity, CommonColors.black)
        outlineWidth = 3f
    }

    // Стиль для альтернативного маршрута
    private fun PolylineMapObject.styleAlternativeRoute() {
        zIndex = 5f
        setStrokeColor(ContextCompat.getColor(this@SecondActivity, CommonColors.red))
        strokeWidth = 4f
        outlineColor = ContextCompat.getColor(this@SecondActivity, CommonColors.black)
        outlineWidth = 2f
    }

    companion object {
        // Координаты офиса компании
        private val DEFAULT_POINTS_COMPANY = Point(56.834218, 60.635329)
    }
}