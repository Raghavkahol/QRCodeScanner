package com.example.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object PermissionUtils {

    fun checkForPermission(activity: Activity, permissionArray: Array<String>, code : Int) {
        ActivityCompat.requestPermissions(
            activity, permissionArray,
            code
        )
    }

}
