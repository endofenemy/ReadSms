package com.test.readsms

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Log

class ReadSmsRepository(private val contentResolver: ContentResolver) {
    suspend fun ReadSmsByNumberAndDays(
        phoneNumber: String,
        days: Int,
    ): ArrayList<SmsModel> {
        val smsList = ArrayList<SmsModel>()
        val inboxURI: Uri = Uri.parse("content://sms/inbox")
        val c: Cursor? = contentResolver.query(inboxURI, null, null, null, null)
        while (c?.moveToNext()!!) {
            val number: String = c.getString(c.getColumnIndexOrThrow("address")).toString()
            val body: String = c.getString(c.getColumnIndexOrThrow("body")).toString()
            val date: String = c.getString(c.getColumnIndexOrThrow("date")).toString()
            val dateSent: String = c.getString(c.getColumnIndexOrThrow("date_sent")).toString()
            val newNumber = if (number.length > 10) number.substring(
                number.length - 10,
                number.length
            ) else number
            if (phoneNumber.equals(newNumber, true) && dateSent.toLong() >= getDateLimit(days))
                smsList.add(SmsModel(number, body, date, dateSent))
        }
        c.close()
        Log.e("SMSs", smsList.toString())
        return smsList
    }

    private fun getDateLimit(days: Int): Long {
        val daysInMillis = java.util.concurrent.TimeUnit.DAYS.toMillis(days.toLong())
        val currentTimeMillis = System.currentTimeMillis()
        return currentTimeMillis - daysInMillis
    }
}