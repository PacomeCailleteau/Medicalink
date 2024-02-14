package dev.mobile.medicalink.fragments.traitements

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import fr.medicapp.medicapp.ai.PrescriptionAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


class LoaderFragment : Fragment() {

    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loader, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLoadingAnimation()

        CoroutineScope(Dispatchers.Main).launch {
            val uri = (arguments?.getString("urimage"))?.toUri()
            val result = withContext(Dispatchers.IO) {
                createTraitement(uri!!)
            }

            handleResult(result)
        }
    }

    private fun handleResult(result: List<String?>) {
        for (i in result.indices) {
            Log.d("RESULT TXT TRAITE", "handleResult: " + result[i])
        }
        val fragTransaction = parentFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.FL, ListeTraitementsFragment())
        fragTransaction.addToBackStack(null)
        fragTransaction.commit()

    }

    private fun startLoadingAnimation() {
        val loaderProgressBar = requireView().findViewById<View>(R.id.loaderProgressBar)
        val fadeInOut = ObjectAnimator.ofFloat(loaderProgressBar, "alpha", 0f, 1f).apply {
            duration = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        handler.postDelayed({
            fadeInOut.start()
        }, 500)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
    /*
    // Fonction qui va lire le texte récupéré depuis l'image et qui fait un traitement après avoir trié les données
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTraitement(text: Uri): List<String?> {
        val myModel = PrescriptionAI(requireContext())
        val texteAnalyze = myModel.analyse(text, onPrediction = { prediction ->
            var treatment = mutableListOf<Traitement>()
            prediction.forEach { (word, label) ->
                when {
                    // B -> début mot
                    // I -> intermédiaire du mot
                    // B-Doliprane I-500mg
                    label.startsWith("B-") -> {
                        // query est la barre de recherche pour associer ce qui est trouver au nom de médicament via un algo
                        // donc cette condition -> si on trouve un nom de medic (drug) et que la barre de recherche est déjà pleine (indiquant qu'il sert déja à quelque chose) -> renvoyer les results + recréer un traitement
                        if (label.removePrefix("B-") == "Drug" && treatment.query.isNotEmpty()) {
                            // query -> suggestion
                            treatment.query = treatment.query.trim()
                            // posology -> quand prendre...
                            treatment.posology = treatment.posology.trim()
                            // renvoyer la page
                            state.treatments.add(treatment)
                            // recrée une instance pour le cas ou il y a plusieurs médic
                            treatment = Treatment()
                        }
                        when (label.removePrefix("B-")) {
                            "Drug" -> treatment.query += " $word"
                            "DrugQuantity" -> treatment.posology += " $word"
                            "DrugForm" -> treatment.posology += " $word"
                            "DrugFrequency" -> treatment.posology += " $word"
                            "DrugDuration" -> treatment.renew += " $word"
                        }
                    }

                    label.startsWith("I-") -> {
                        when (label.removePrefix("I-")) {
                            "Drug" -> treatment.query += " $word"
                            "DrugQuantity" -> treatment.posology += " $word"
                            "DrugForm" -> treatment.posology += " $word"
                            "DrugFrequency" -> treatment.posology += " $word"
                            "DrugDuration" -> treatment.renew += " $word"
                        }
                    }
                }
            }
            // dernier élément
            if (treatment.query.isNotEmpty()) {
                treatment.query = treatment.query.trim()
                treatment.posology = treatment.posology.trim()
                state.treatments.add(treatment)
            }
        },
            // retirer le loader
            onDismiss = {
                loading.value = false
            })
        return texteAnalyze
    }

     */

    // Fonction qui va lire le texte récupéré depuis l'image et qui fait un traitement après avoir trié les données
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTraitement1(text: Uri): List<String?> {
        val myModel = PrescriptionAI(requireContext())
        val texteAnalyze = myModel.analyse(text, onPrediction = { prediction ->
            var treatment = mutableListOf(Traitement(
                "",
                "",
                0,
                "",
                null,
                comprimesRestants = null,
                effetsSecondaires = null,
                totalQuantite = null,
                UUID = null,
                UUIDUSER = null,
                dateDbtTraitement = null
            ))
            var last = 0
            prediction.forEach { (word, label) ->
                when {
                    // B -> début mot
                    // I -> intermédiaire du mot
                    // B-Doliprane I-500mg
                    label.startsWith("B-") -> {
                        // query est la barre de recherche pour associer ce qui est trouver au nom de médicament via un algo
                        // donc cette condition -> si on trouve un nom de medic (drug) et que la barre de recherche est déjà pleine (indiquant qu'il sert déja à quelque chose) -> renvoyer les results + recréer un traitement
                        if (label.removePrefix("B-") == "Drug" && treatment[last].nomTraitement != "") {

                            treatment.add(
                                Traitement(
                                    "",
                                    "",
                                    0,
                                    "",
                                    null,
                                    comprimesRestants = null,
                                    effetsSecondaires = null,
                                    totalQuantite = null,
                                    UUID = null,
                                    UUIDUSER = null,
                                    dateDbtTraitement = null
                                )
                            )
                            last = treatment.size-1

                        }
                        when (label.removePrefix("B-")) {
                            "Drug" -> treatment[last].nomTraitement = word
                            "DrugQuantity" -> treatment[last].dosageNb = word.toInt()
                            "DrugForm" -> treatment[last].typeComprime = word
                            "DrugFrequency" -> treatment[last].dosageUnite = word
                            "DrugDuration" -> treatment[last].suggDuree = word
                        }
                    }

                    label.startsWith("I-") -> {
                        when (label.removePrefix("I-")) {
                            "Drug" -> treatment[last].nomTraitement += " $word"
                            "DrugQuantity" -> treatment[last].dosageNb = word.toInt()
                            "DrugForm" -> treatment[last].typeComprime = " $word"
                            "DrugFrequency" -> treatment[last].dosageUnite = " $word"
                            "DrugDuration" -> treatment[last].suggDuree += " $word"
                        }
                    }
                }
            }
            //lancer pour chaque élément le traitement du traitement
        },
            // retirer le loader
            onDismiss = {
                loading.value = false
            })
        return texteAnalyze
    }
}

