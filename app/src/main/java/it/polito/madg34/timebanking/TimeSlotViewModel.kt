package it.polito.madg34.timebanking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeSlotViewModel: ViewModel() {


    var _title_vm = MutableLiveData<String>().also{it.value = "Lucio"}
    var _description_vm = MutableLiveData<String>().also{it.value = "momomoomomomm"}
    var _date_vm = MutableLiveData<String>().also{it.value = "06/09/2020"}
    var _time_vm = MutableLiveData<String>().also{it.value = "15:30"}
    var _duration_vm = MutableLiveData<String>().also{it.value = "1h"}
    var _location_vm = MutableLiveData<String>().also{it.value = "Torino"}

    val title_vm : LiveData<String> = _title_vm
    val description_vm : LiveData<String> = _description_vm
    val date_vm : LiveData<String> = _date_vm
    val time_vm : LiveData<String> = _time_vm
    val duration_vm : LiveData<String> = _duration_vm
    val location_vm : LiveData<String> = _location_vm

    fun m_title(s: String){
        _title_vm.value = s
    }

    fun m_description(s: String){
        _description_vm.also { it.value = s }
    }

    fun m_date(s: String){
        _date_vm.also { it.value = s }
    }

    fun m_time(s: String){
        _time_vm.also { it.value = s }
    }

    fun m_duration(s: String){
        _duration_vm.also { it.value = s }
    }

    fun m_location(s: String){
        _location_vm.also { it.value = s }
    }




}