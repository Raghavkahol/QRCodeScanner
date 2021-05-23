package com.example.scanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var location_id : String? = null,
    var location_detail: String? = null,
    var price_per_min: Float? = null,
    var start_time: Long? = null,
    var end_time: Long? = null,
    var time_spent: Int? = null,
    var is_session_progress: Boolean? = false
)
