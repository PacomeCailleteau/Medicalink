package dev.mobile.medicalink.fragments.traitements.ajouts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.time.LocalDate


class AjoutSharedViewModel : ViewModel() {

    private val _nomTraitement = MutableLiveData<String>("")
    val nomTraitement: LiveData<String> get() = _nomTraitement
    fun setNomTraitement(nom: String) {
        _nomTraitement.value = nom
    }

    private val _codeCIS = MutableLiveData<String>("")
    val codeCIS: LiveData<String> get() = _codeCIS
    fun setCodeCIS(code: String) {
        _codeCIS.value = code
    }

    private val _dosageNb = MutableLiveData<Int>(0)
    val dosageNb: LiveData<Int> get() = _dosageNb
    fun setDosageNb(dosage: Int) {
        _dosageNb.value = dosage
    }

    private val _frequencePrise = MutableLiveData<String>("")
    val frequencePrise: LiveData<String> get() = _frequencePrise
    fun setFrequencePrise(unite: String) {
        _frequencePrise.value = unite
    }

    private val _dateFinTraitement = MutableLiveData<LocalDate?>()
    val dateFinTraitement: LiveData<LocalDate?> get() = _dateFinTraitement
    fun setDateFinTraitement(date: LocalDate?) {
        _dateFinTraitement.value = date
    }

    private val _typeComprime = MutableLiveData<String>("")
    val typeComprime: LiveData<String> get() = _typeComprime
    fun setTypeComprime(type: String) {
        _typeComprime.value = type
    }

    private val _comprimesRestants = MutableLiveData<Int>(0)
    val comprimesRestants: LiveData<Int> get() = _comprimesRestants
    fun setComprimesRestants(comprimes: Int) {
        _comprimesRestants.value = comprimes
    }

    private val _effetsSecondaires = MutableLiveData<MutableList<String>>(mutableListOf())
    val effetsSecondaires: LiveData<MutableList<String>> get() = _effetsSecondaires
    fun setEffetsSecondaires(effets: MutableList<String>) {
        _effetsSecondaires.value = effets
    }

    private val _prises = MutableLiveData<MutableList<Prise>>(mutableListOf())
    val prises: LiveData<MutableList<Prise>> get() = _prises
    fun setPrises(prises: MutableList<Prise>) {
        _prises.value = prises
    }

    private val _totalQuantite = MutableLiveData<Int>(0)
    val totalQuantite: LiveData<Int> get() = _totalQuantite
    fun setTotalQuantite(quantite: Int) {
        _totalQuantite.value = quantite
    }

    private val _UUID = MutableLiveData<String>("")
    val UUID: LiveData<String> get() = _UUID
    fun setUUID(uuid: String) {
        _UUID.value = uuid
    }

    private val _UUIDUSER = MutableLiveData<String>("")
    val UUIDUSER: LiveData<String> get() = _UUIDUSER
    fun setUUIDUSER(uuid: String) {
        _UUIDUSER.value = uuid
    }

    private val _dateDbtTraitement = MutableLiveData<LocalDate>(null)
    val dateDbtTraitement: LiveData<LocalDate> get() = _dateDbtTraitement
    fun setDateDbtTraitement(date: LocalDate) {
        _dateDbtTraitement.value = date
    }

    private val _traitements = MutableLiveData<MutableList<Traitement>>(mutableListOf())
    val traitements: LiveData<MutableList<Traitement>> get() = _traitements
    fun setTraitements(traitements: MutableList<Traitement>) {
        _traitements.value = traitements
    }

    private val _isAddingTraitement = MutableLiveData<Boolean?>(null)
    val isAddingTraitement: LiveData<Boolean?> get() = _isAddingTraitement
    fun setIsAddingTraitement(isAdding: Boolean?) {
        _isAddingTraitement.value = isAdding
    }

    private val _schema_prise1 = MutableLiveData<String>("")
    val schema_prise1: LiveData<String> get() = _schema_prise1
    fun setSchemaPrise1(schema: String) {
        _schema_prise1.value = schema
    }

    private val _provenance = MutableLiveData<String>("")
    val provenance: LiveData<String> get() = _provenance
    fun setProvenance(provenance: String) {
        _provenance.value = provenance
    }

    fun makeTraitement() = Traitement(
        nomTraitement.value ?: "",
        codeCIS.value ?: "",
        dosageNb.value ?: 0,
        frequencePrise.value ?: "",
        dateFinTraitement.value,
        typeComprime.value ?: "",
        comprimesRestants.value ?: 0,
        effetsSecondaires = effetsSecondaires.value ?: mutableListOf(),
        prises = prises.value ?: mutableListOf(),
        totalQuantite = totalQuantite.value ?: 0,
        uuid = UUID.value ?: "",
        uuidUser = UUIDUSER.value ?: "",
        dateDbtTraitement = dateDbtTraitement.value ?: LocalDate.now()
    )

    fun addTraitement(traitement: Traitement = makeTraitement()) {
        val list = _traitements.value
        if (list != null) {
            list.add(traitement)
            setTraitements(list)
        } else {
            setTraitements(mutableListOf(traitement))
        }
    }

    fun removeTraitement(traitement: Traitement) {
        val list = _traitements.value
        if (list != null) {
            list.remove(traitement)
            setTraitements(list)
        }
    }

    fun reset() {
        setNomTraitement("")
        setCodeCIS("")
        setDosageNb(0)
        setFrequencePrise("")
        setDateFinTraitement(null)
        setTypeComprime("")
        setComprimesRestants(0)
        setEffetsSecondaires(mutableListOf())
        setPrises(mutableListOf())
        setTotalQuantite(0)
        setUUID("")
        setUUIDUSER("")
        setDateDbtTraitement(LocalDate.now())
        setSchemaPrise1("")
        setProvenance("")
        setIsAddingTraitement(null)
    }

}