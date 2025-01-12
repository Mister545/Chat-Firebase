package com.example.chatfirebase

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataTimeHelper {

        // Повертає поточну дату та час у стандартному форматі
        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            return sdf.format(Calendar.getInstance().time)
        }

        // Повертає поточну дату у форматі
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return sdf.format(Calendar.getInstance().time)
        }

        // Повертає поточний час у форматі
        fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            return sdf.format(Calendar.getInstance().time)
        }

        // Повертає дату у потрібному форматі
        fun formatCustomDate(date: Date, pattern: String): String {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            return sdf.format(date)
        }

    @SuppressLint("NewApi")
    fun getIsoUtcFormat(): String {
        // Отримати поточний час у UTC
        val utcTime = Instant.now()

        // Форматувати у ISO-8601
        val formatter = DateTimeFormatter.ISO_INSTANT
        return formatter.format(utcTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIsoUtcFToLocal(time: String): String {
        val utcTime = Instant.parse(time) // або завантажений з бази
        val localTime = utcTime.atZone(ZoneId.systemDefault()) // Конвертуємо в локальний час
        val formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        return formattedTime
    }
}