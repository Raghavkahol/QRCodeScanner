package com.example.scanner

import android.app.Activity
import androidx.core.app.ActivityCompat

object PermissionUtils {

    fun checkForPermission(activity: Activity, permissionArray: Array<String>) {
        ActivityCompat.requestPermissions(
            activity, permissionArray,
            101
        )
    }

}
