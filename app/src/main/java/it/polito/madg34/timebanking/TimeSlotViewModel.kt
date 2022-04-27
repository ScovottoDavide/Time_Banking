package it.polito.madg34.timebanking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotViewModel: ViewModel() {
    val title_vm = MutableLiveData<String>().also{it.value = "Lucio"}
    val description_vm = MutableLiveData<String>().also{it.value = "momomoomomomm"}
    val date_vm = MutableLiveData<String>().also{it.value = "06/09/2020"}
    val time_vm = MutableLiveData<String>().also{it.value = "15:30"}
    val duration_vm = MutableLiveData<String>().also{it.value = "1h"}
    val location_vm = MutableLiveData<String>().also{it.value = "Torino"}



}