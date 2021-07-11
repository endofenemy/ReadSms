package com.test.readsms

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReadSmsModelFactory(private val readSmsRepository: ReadSmsRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReadSmsViewModel(readSmsRepository) as T
    }
}