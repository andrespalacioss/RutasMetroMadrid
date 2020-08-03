package com.example.rutasmetromadrid

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Rutas : AppCompatActivity() {

    var linea: String? = null
    lateinit var paradas: Array<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rutas)

        var miIntento = this.intent
        linea=miIntento.getStringExtra("LINEAS")
        findViewById<TextView>(R.id.linea).text = linea

        var miBundle = intent.extras
        var datos = miBundle?.getParcelableArray("PARADAS")
        var ruta = Arrays.copyOf(datos, datos?.count()!!, Array<Location>::class.java)

        var rutaContenedor = findViewById<LinearLayout>(R.id.pantalla)
        var inflador = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        pintaEtapa(inflador,R.drawable.esferainiciofin,ruta[0].provider,rutaContenedor)

        var texto = "${getString(R.string.a_pie)} ${ruta[0].distanceTo(ruta[1]).toInt()} ${getString(R.string.metros)}"
        pintaEtapa(inflador,R.drawable.caminando,texto,rutaContenedor)

        for (i in 1 until ruta.count() -1 ){
            pintaEtapa(inflador,R.drawable.esferaetapa,ruta[i].provider,rutaContenedor)
        }

        if(ruta.count() > 2){
            texto = "${getString(R.string.a_pie)} ${ruta[ruta.count()-2].distanceTo(ruta[ruta.count()-1])} ${getString(R.string.metros)}"
            pintaEtapa(inflador,R.drawable.caminando,texto,rutaContenedor)
        }

        pintaEtapa(inflador,R.drawable.esferainiciofin,ruta[ruta.count()-1].provider,rutaContenedor)


    }




    fun pintaEtapa (inflador: LayoutInflater,imagen: Int, texto:String, contenedor: LinearLayout){
        var distanciaEstaciones = inflador.inflate(R.layout.distancia_estaciones,null) as LinearLayout
        distanciaEstaciones.findViewById<ImageView>(R.id.icono).setImageResource(imagen)
        distanciaEstaciones.findViewById<TextView>(R.id.texto).text =texto
        contenedor.addView(distanciaEstaciones)
    }


    fun mapa(vista: View){
        var miBundle = intent.extras
        var miIntento: Intent = Intent(this, MapsActivity::class.java)
        miIntento.putExtras(miBundle!!)
        startActivity(miIntento)
    }


    fun mejorRuta(origen: Location, destino: Location, lasLineas: Array<Lineas>){

        var mejorLinea: Lineas? = null
        var size = 0
        var vRutaAscendente = false

        lasLineas.forEach {
            it.distancias(origen,destino)

            if (mejorLinea == null || it.sumaDistanciaMetros() < mejorLinea!!.sumaDistanciaMetros() ){
                mejorLinea = it
            }

        }

        if (mejorLinea == null || origen.distanceTo(destino) < mejorLinea?.sumaDistanciaMetros()!!){
            linea = null
            paradas = Array<Location>(2) { Location("") }
            paradas[0] = origen
            paradas[1] = destino
            return
        }

        if(mejorLinea?.vFinalRUta!! > mejorLinea?.vOrigenRUta!!){
             size = mejorLinea?.vFinalRUta?.minus(mejorLinea?.vOrigenRUta!!)?.plus(3)!!
            vRutaAscendente = true
        }else size = mejorLinea?.vOrigenRUta?.minus(mejorLinea?.vFinalRUta!!)?.plus(3)!!


        linea = mejorLinea!!.vNombre
        paradas = Array<Location>(size) { Location("") }

        paradas[0] = origen
        paradas[paradas.count() - 1] = destino

        var pos = 0
            for (i in 1 until paradas.count()-1){
                if (vRutaAscendente){
                    paradas[i] = mejorLinea?.vEstaciones?.get(mejorLinea?.vOrigenRUta!! + i - 1)!!

                }else{
                    pos = mejorLinea?.vOrigenRUta!! - i + 1
                    paradas[i] = mejorLinea?.vEstaciones?.get(pos)!!

                }
            }


    }
}