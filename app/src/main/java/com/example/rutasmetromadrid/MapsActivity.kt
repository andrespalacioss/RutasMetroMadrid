package com.example.rutasmetromadrid


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            println("*************PERMISO DENEGADO**********************")
           return
        }else mMap.isMyLocationEnabled = true


        val miBundle = intent.extras
        val datos = miBundle?.getParcelableArray("PARADAS")
        val ruta = Arrays.copyOf(datos, datos?.count()!!,Array<Location>::class.java)

        mMap.clear()

        var posicion: LatLng
        var anteriorPosicion: LatLng

        var matizColor = BitmapDescriptorFactory.HUE_GREEN

        posicion = LatLng(ruta[0].latitude,ruta[0].longitude)

        mMap.addMarker(MarkerOptions().position(posicion).title(ruta[0].provider).icon(BitmapDescriptorFactory.defaultMarker(matizColor)))

        var color = Color.RED

        for (i in 1 until ruta.count()){
            matizColor = BitmapDescriptorFactory.HUE_RED
            anteriorPosicion = posicion
            posicion = LatLng(ruta[i].latitude,ruta[i].longitude)

            if(i==ruta.count()-1) matizColor = BitmapDescriptorFactory.HUE_GREEN


            mMap.addPolyline(PolylineOptions().add(anteriorPosicion,posicion).width(7f).color(color).geodesic(true))

            mMap.addMarker(MarkerOptions().position(posicion).title(ruta[i].provider).icon(BitmapDescriptorFactory.defaultMarker(matizColor)))
        }
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(LatLng(ruta[0].latitude,ruta[0].longitude),15f)))

    }


}