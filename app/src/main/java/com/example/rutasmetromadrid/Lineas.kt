package com.example.rutasmetromadrid

import android.location.Location
import android.os.Parcel
import android.os.Parcelable


class Lineas() : Parcelable {

    lateinit var vNombre: String
    var vOrigenRUta: Int = 0
    var vFinalRUta: Int = 0
    private var vDatosParadaOrigen: Float = 0.0f
    private var vDatosParadaDestino: Float = 0.0f
    lateinit var vEstaciones: Array<Location>

    constructor(parcel: Parcel) : this() {

        vNombre = parcel.readString().toString()
        vOrigenRUta = parcel.readInt()
        vFinalRUta = parcel.readInt()
        vDatosParadaOrigen = parcel.readFloat()
        vDatosParadaDestino = parcel.readFloat()
        vEstaciones = parcel.createTypedArray(Location.CREATOR) as Array<Location>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vNombre)
        parcel.writeInt(vOrigenRUta)
        parcel.writeInt(vFinalRUta)
        parcel.writeFloat(vDatosParadaOrigen)
        parcel.writeFloat(vDatosParadaDestino)
        parcel.writeTypedArray(vEstaciones, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Lineas> {

        override fun createFromParcel(paquete: Parcel): Lineas {

            return Lineas(paquete)
        }

        override fun newArray(size: Int): Array<Lineas?> {
            return arrayOfNulls(size)
        }
    }


    fun distancias(origen: Location, destino: Location){
        vDatosParadaOrigen = origen.distanceTo(vEstaciones[0])
        vDatosParadaDestino = destino.distanceTo(vEstaciones[0])

        for (i in 1 until vEstaciones.count()){
            if (origen.distanceTo(vEstaciones[i])<vDatosParadaOrigen){
                vOrigenRUta = i
                vDatosParadaOrigen = origen.distanceTo(vEstaciones[i])
            }

            if (destino.distanceTo(vEstaciones[i])<vDatosParadaDestino){
                vFinalRUta = i
                vDatosParadaDestino = destino.distanceTo(vEstaciones[i])
            }


        }
    }

    fun sumaDistanciaMetros() = vDatosParadaOrigen.plus(vDatosParadaDestino)

}