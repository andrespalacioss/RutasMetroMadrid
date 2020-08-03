package com.example.rutasmetromadrid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.*

import java.lang.Double.parseDouble

class ManejoBBDD(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var rutaAlmacenamiento = "${context.filesDir.parentFile.path}/$DATABASE_NAME"

    private lateinit var bbdd: SQLiteDatabase

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use {
                        it.copyTo(cache)
                    }
                }
            }
        }



    fun aperturaBBDD(context: Context){

    /*
        var dir = File(rutaAlmacenamiento)
        if (dir.exists()) println("****************ARCHIVO EXISTE!***************************")
*/
        try {

            bbdd = SQLiteDatabase.openDatabase(rutaAlmacenamiento,null, SQLiteDatabase.OPEN_READONLY)
        }catch (e: Exception){
            copiaBBDD(context)
            bbdd = SQLiteDatabase.openDatabase(rutaAlmacenamiento,null, SQLiteDatabase.OPEN_READONLY)
        }
    }

    private fun copiaBBDD(context: Context){

        var vDatosEntrada: InputStream = context.assets.open("paradasmetro.db3")
        var vDatosSalida: OutputStream =  FileOutputStream(rutaAlmacenamiento)
        var vBufferBBDD  = ByteArray(1024)
        try {

            var length = vDatosEntrada.read(vBufferBBDD)
            while (length>0){
                vDatosSalida.write(vBufferBBDD,0,length)
                length = vDatosEntrada.read(vBufferBBDD)
            }
            vDatosEntrada.close()
            vDatosSalida.flush()
            vDatosSalida.close()
        }catch (e: Exception){

            Toast.makeText(context,"Algo pasó aquí",Toast.LENGTH_LONG).show()
        }

    }

    private fun DatosEstacion(id: Int): Location{
        var vEstacion: Location
        var miCursor: Cursor = bbdd.rawQuery("SELECT * FROM Paradas WHERE id='$id'", null )
        miCursor.moveToNext()

        vEstacion = Location(miCursor.getString(1).toString()) //aquí se le pasa el nombre de la estacion

        vEstacion.latitude = miCursor.getString(2).toDouble()
        vEstacion.longitude = miCursor.getString(3).toDouble()

        miCursor.close()

        return vEstacion

    }


    fun dameInfoLineas(vNombreDeLineas: Array<String>): Array<Lineas> {

        var vLasLineas = Array<Lineas>(vNombreDeLineas.count()) { Lineas() }
        var miCursor: Cursor?
        for (i in vNombreDeLineas.indices){
            vLasLineas[i] = Lineas()
            vLasLineas[i].vNombre = vNombreDeLineas[i]

            miCursor = bbdd.rawQuery("SELECT id FROM " + vNombreDeLineas[i], null)
            vLasLineas[i].vEstaciones = Array<Location>(miCursor.count) { Location("") }


            var vContador: Int = 0
            miCursor.moveToFirst()

            while (!miCursor.isAfterLast){
                var vEstacion = miCursor.getString(0).toInt()
                vLasLineas[i].vEstaciones[vContador] = DatosEstacion(vEstacion)
                vContador ++
                miCursor.moveToNext()
            }
            if (miCursor!=null && !miCursor.isClosed) miCursor.close()
        }

       return vLasLineas
    }

    fun CerrarBBDD(){
        bbdd.close()
    }


    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")



    }

    override fun onUpgrade(db: SQLiteDatabase?, viejo: Int, nuevo: Int) {
        TODO("Not yet implemented")
    }

    companion object {
           const val DATABASE_VERSION = 1
           const val DATABASE_NAME = "paradasmetro.db3"


    }
}