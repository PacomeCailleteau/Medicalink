package dev.mobile.medicalink.fragments.traitements.ajouts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AjoutSharedViewModel : ViewModel() {
    private val _selectedTypeMedic = MutableLiveData<String>()
    val selectedTypeMedic: LiveData<String> get() = _selectedTypeMedic
    fun setSelectedTypeMedic(type: String) {
        _selectedTypeMedic.value = type
    }

    private val _selectedHeurePrise = MutableLiveData<String>()
    val selectedHeurePrise: LiveData<String> get() = _selectedHeurePrise
    fun setSelectedHeurePrise(heure: String) {
        _selectedHeurePrise.value = heure
    }

    private val _selectedQuantite = MutableLiveData<String>()
    val selectedQuantite: LiveData<String> get() = _selectedQuantite
    fun setSelectedQuantite(quantite: String) {
        _selectedQuantite.value = quantite
    }

}