package com.example.rutasmetromadrid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.rutasmetromadrid.extension.openAppSettings
import com.example.rutasmetromadrid.extension.toast
import java.util.*

class MainActivity : AppCompatActivity() {


    private var vLineasMetro = arrayListOf<String>("Linea2","Linea3","Linea6")
    private lateinit var vLineas: Array<Lineas>
    private var barraProgreso: ProgressBar? = null


    private val coarsePermission = PermisionRequester (
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        onRational = { toast("Show Rational")},
        onDenied = { toast("Denied") }
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        coarsePermission.runWithPermission { toast("Granted") }
        barraProgreso = findViewById(R.id.pbProgreso)
        barraProgreso!!.visibility = View.VISIBLE

        var comienzo: Sincroniza = Sincroniza()
        comienzo.execute()

    }



    @SuppressLint("StaticFieldLeak")
    inner class Sincroniza: AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String? {

            var bbdd: ManejoBBDD = ManejoBBDD(applicationContext)
            try {

                bbdd.aperturaBBDD(applicationContext)


                vLineas = bbdd.dameInfoLineas(vLineasMetro.toTypedArray())

                bbdd.CerrarBBDD()

            }catch(e: Exception){
                finish()
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            barraProgreso!!.setProgress(values[0]!!,true)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            comenzar()
        }

    }

    fun comenzar(){
        var miBundle = Bundle()
        miBundle.putParcelableArray("LINEAS",vLineas)
        var miIntent: Intent? = Intent(this, Buscador::class.java)
        miIntent?.putExtras(miBundle)
        startActivity(miIntent)
    }



    class PermisionRequester(
        private var activity: ComponentActivity,
        private val permission: String,
        private val onRational: ()-> Unit = {},
        private val onDenied: ()-> Unit = {}
    ){
        private var onGranted: () -> Unit = {}

        @RequiresApi(Build.VERSION_CODES.M)
        private val permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            when{
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onRational()
                else -> onDenied()
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun runWithPermission(body: ()->Unit){
            onGranted = body
            permissionLauncher.launch(permission)
        }
    }


}