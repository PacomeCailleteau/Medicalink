package dev.mobile.medicalink.utils
import dev.mobile.medicalink.R

class MapIconeMedecin : HashMap<String, Int>() {
    init {
        this["Médecin"] = R.drawable.docteur
        this["Docteur"] = R.drawable.docteur
        this["Pharmacien"] = R.drawable.pharmacien
        this["Podologue"] = R.drawable.podologue
        this["Pédicure"] = R.drawable.podologue
        this["Kiné"] = R.drawable.kine
        this["Masseur"] = R.drawable.kine
        this["Dentiste"] = R.drawable.dentiste
        this["Neurologie"] = R.drawable.psychologue
        this["Chirurgie"] = R.drawable.chirurgien
        this["Oto-rhino"] = R.drawable.medecin_oreille
        this["Laryngo"] = R.drawable.medecin_oreille
        this["Cardio"] = R.drawable.cardiologue
        this["Radio"] = R.drawable.radiologue
        this["Dentiste"] = R.drawable.dentiste
        this["Ophtalmolo"] = R.drawable.ophtalmologie
        this["Psych"] = R.drawable.psychologue
        this["social"] = R.drawable.psychologue
        this["O.R.L"] = R.drawable.medecin_oreille
        this["ORL"] = R.drawable.medecin_oreille
        this["Gynéco"] = R.drawable.gynecologue
        this["Sage"] = R.drawable.sage_femme
    }
}