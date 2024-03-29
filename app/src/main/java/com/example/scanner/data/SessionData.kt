package com.example.scanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SessionData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var location_id : String? = null,
    @SerializedName(value="location_details", alternate= ["location_detail"])
    var location_details: String? = null,
    var price_per_min: Float? = null,
    var start_time: Long = 0L,
    var end_time: Long = 0L,
    var time_spent: Int? = null,
    var is_session_progress: Boolean? = false
)
