package com.test.readsms

import java.io.Serializable

data class SmsModel(
    val number:String,
    val body:String,
    val date:String,
    val dateSent:String,
):Serializable