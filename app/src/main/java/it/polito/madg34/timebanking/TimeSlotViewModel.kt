package it.polito.madg34.timebanking

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotViewModel: ViewModel() {
    val title_vm = MutableLiveData<String>().also{it.value = "Lucio"}
    val description_vm = MutableLiveData<String>().also{it.value = "momomoomomomm"}
    val date_vm = MutableLiveData<String>().also{it.value = "06/09/2020"}
    val time_vm = MutableLiveData<String>().also{it.value = "15:30"}
    val duration_vm = MutableLiveData<String>().also{it.value = "1h"}
    val location_vm = MutableLiveData<String>().also{it.value = "Torino"}

    fun m_title(s: String){
        title_vm.also { it.value = s }


    }

    fun m_description(s: String){
        description_vm.value = s
    }

    fun m_date(s: String){
        date_vm.value = s
    }

    fun m_time(s: String){
        time_vm.value = s
    }

    fun m_duration(s: String){
        duration_vm.value = s
    }
    fun m_location(s: String){
        location_vm.value = s
    }


}