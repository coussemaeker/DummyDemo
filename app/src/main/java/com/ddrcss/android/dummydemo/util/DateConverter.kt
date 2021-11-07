package com.ddrcss.android.dummydemo.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {

    private val ISO_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").also { it.timeZone = TimeZone.getTimeZone("UTC") }
    private val USER_DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun convertIsoDate(isoDate: String): String {
        try {
            return USER_DATE_FORMAT.format(ISO_FORMAT.parse(isoDate))
        } catch (parseException: ParseException) {
            return isoDate
        }
    }

}