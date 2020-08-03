package com.example.rutasmetromadrid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.*

class Buscador: AppCompatActivity(){

    lateinit var vlineas: Array<Lineas>
    var vRuta: Rutas = Rutas()
    var barraProgreso: ProgressBar? = null
    private lateinit var txtOrigen: TextView
    private lateinit var txtDestino: TextView
    lateinit var btnEnviar: Button
    lateinit var vDireccionOrigen: String
    lateinit var vDireccionDestino: String
    lateinit var  miIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscador)
        miIntent = intent
        vlineas = emptyArray()
    }


    override fun onResume() {
        super.onResume()

        txtOrigen  = findViewById(R.id.origen)
        txtDestino = findViewById(R.id.destino)

        txtOrigen.text = ""
        txtDestino.text = ""


        btnEnviar = findViewById(R.id.enviar)
        btnEnviar.alpha = 1f
        btnEnviar.isEnabled = true


        if (vlineas.isNullOrEmpty()){


            var miBundle = intent.extras

            var misDatos = miBundle?.getParcelableArray("LINEAS")

            if (misDatos != null) {
                vlineas = Arrays.copyOf(misDatos, misDatos.size, Array<Lineas>::class.java)
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun leeDirecciones(vista: View){
        vDireccionOrigen = txtOrigen.text.toString()
        vDireccionDestino = txtDestino.text.toString()

        if (vDireccionOrigen.isNotEmpty() && vDireccionDestino.isNotEmpty()) {
            btnEnviar.alpha = 0.5f
            btnEnviar.isEnabled = false
            //btnEnviar.isClickable = false

            var introduce: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            introduce.hideSoftInputFromWindow(window.decorView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            barraProgreso = findViewById(R.id.progressBar)
            barraProgreso = ProgressBar(this)
            barraProgreso!!.visibility = vista.visibility


            val tarea = EjecutaSegundoPlano()
            tarea.execute()
        }else Toast.makeText(this,"Ingresa Datos!",Toast.LENGTH_SHORT).show()






    }

    @SuppressLint("StaticFieldLeak")
    inner class EjecutaSegundoPlano: AsyncTask<String, Int, String>() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun doInBackground(vararg params: String?): String? {
            var puntoOrigen: Location?
            var puntoDestino: Location?

            var contexto: Context =  applicationContext
            var miManager: ConnectivityManager = contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            var estadored = miManager.activeNetwork

            if (estadored == null ){
                return getString(R.string.error_conexion)
            }


            try {
                puntoOrigen = OptimizacionBusqueda_.busca(vDireccionOrigen)
                if (puntoOrigen == null) return getString(R.string.error_origen)

                puntoDestino = OptimizacionBusqueda_.busca(vDireccionDestino)
                if (puntoDestino == null) return getString(R.string.error_destino)

            }catch (e: Exception){
                return getString(R.string.error_red)
            }

            vRuta.mejorRuta(puntoOrigen, puntoDestino, vlineas)

            return null
        }


        @RequiresApi(Build.VERSION_CODES.N)
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            barraProgreso!!.setProgress(values[0]!!,true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            barraProgreso = null
            btnEnviar.alpha = 1f
            btnEnviar.isEnabled = true

            if (!result.isNullOrEmpty()){
                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
            }else muestraRuta()



        }

    }

    fun muestraRuta(){

        var miIntent = Intent(this,Rutas::class.java)
        miIntent.putExtra("LINEAS",vRuta.linea)
        miIntent.putExtra("PARADAS",vRuta.paradas)

        startActivity(miIntent)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(barraProgreso != null) barraProgreso = null
        moveTaskToBack(true)
    }


}

