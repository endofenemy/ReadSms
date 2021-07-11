package com.test.readsms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReadSmsViewModel(
    val readSmsRepository: ReadSmsRepository
) : ViewModel() {
    var phoneNumber: String = ""
    var numberOfDays: String = ""

    private val _phoneError = MutableLiveData<String>()
    val phoneError: LiveData<String>
        get() = _phoneError

    private val _daysError = MutableLiveData<String>()
    val daysError: LiveData<String>
        get() = _daysError

    private val _buttonClick = MutableLiveData<Boolean>()
    val buttonClick: LiveData<Boolean>
        get() = _buttonClick

    private val _smsList = MutableLiveData<ArrayList<SmsModel>>()
    val smsList: LiveData<ArrayList<SmsModel>>
        get() = _smsList


    fun calculate() {
        if (!validatePhoneNumber()) {
            _phoneError.value = "Please Enter Valid Phone Numnber"
        } else if (!validateNumberOfDays()) {
            _daysError.value = "Please Enter valid number of days"
        } else {
            _buttonClick.value = true
        }
    }

    private fun validateNumberOfDays(): Boolean {
        return numberOfDays.isNotEmpty() && numberOfDays.toInt() > 0
    }

    private fun validatePhoneNumber(): Boolean {
        return phoneNumber.length == 10
    }

    fun permissionGranted() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                readSmsRepository.ReadSmsByNumberAndDays(phoneNumber, numberOfDays.toInt())
            Log.e("View Model Response ", response.toString())
            _smsList.postValue(response)
        }
    }

}