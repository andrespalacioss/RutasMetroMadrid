package com.example.rutasmetromadrid.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast


fun Context.toast(mensaje: String){
    Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
}

fun Context.openAppSettings(){
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:$packageName")
    }.let(::startActivity)
}