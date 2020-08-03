package com.example.rutasmetromadrid

import android.content.Context
import android.location.Location
import android.os.NetworkOnMainThreadException
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import android.content.res.Resources

object OptimizacionBusqueda_{

    fun busca(vDireccion: String): Location? {

        var direccion  = vDireccion

        var centroCiudad: Location = Location("")
        centroCiudad.latitude = 40.4381311
        centroCiudad.longitude = -3.8196205

        direccion = "$direccion, Madrid"

        var localizacion: Location?

        try {
            localizacion = consultaLocalizacion ("$direccion", centroCiudad)
            return localizacion
        }catch (e: Exception){
            return null
        }
    }

    @Throws(IOException::class)
    fun consultaLocalizacion(direccion: String, centroCiudad: Location): Location?{
        var localizacion: Location? = null
        var entradaDatos: InputStream
        var cliente: HttpsURLConnection? = null


        try {
            var url: URL = URL("https://maps.google.com/maps/api/geocode/json?address=${URLEncoder.encode(direccion, "UTF-8")}&components=locality:madrid|country:ES&key=")
            cliente = url.openConnection() as HttpsURLConnection
            cliente.connect()

            val responseCode = cliente.responseCode

            if (responseCode != HttpsURLConnection.HTTP_OK){
                throw IOException("HTTP error code: $responseCode")
            }

            var cadena: StringBuilder = StringBuilder()
            entradaDatos = BufferedInputStream(cliente.inputStream)
            do {
                val c = entradaDatos.read()
                if (c != -1) cadena.append(c.toChar())
            } while (c != -1)

            var objetoJSON = JSONObject(cadena.toString())
            //if (objetoJSON.getString("status") == "ZERO_RESULTS") return null
            if (objetoJSON.getString("status") != "OK") return null
            var direcciones = objetoJSON.getJSONArray("results")
            if (direcciones.length()==0) return null
            localizacion = getLocalizacion(direcciones.getJSONObject(0)!!)
            return localizacion!!
        }catch (e: NetworkOnMainThreadException){
            println("PROBLEMA DE CONEXION: $e")
        }finally {
            cliente?.inputStream?.close()
            cliente?.disconnect()
        }
        return localizacion!!
    }


    fun getLocalizacion(dire: JSONObject): Location?{

        //Extrae Direcciones
        var direccion = dire.getString("formatted_address")

        //COnvierte la direccion extraida a  UTF8
        direccion = String(direccion.toByteArray(Charsets.UTF_8))

        var localizacion = Location(direccion)

        var latitud = dire.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
        var longitud = dire.getJSONObject("geometry").getJSONObject("location").getDouble("lng")

        localizacion.longitude = longitud
        localizacion.latitude = latitud

        return localizacion

    }
}