package dev.mobile.medicalink.fragments.traitements

import android.animation.ObjectAnimator
import android.content.Context
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
        // Méthode appelée pour créer la vue du fragment
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loader, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Méthode appelée lorsque la vue est créée
        super.onViewCreated(view, savedInstanceState)

        startLoadingAnimation()

        CoroutineScope(Dispatchers.Main).launch {
            val uri = (arguments?.getString("urimage"))?.toUri()
            val result = withContext(Dispatchers.IO) {
                createTraitement(uri!!, view.context)
            }

            handleResult(result)
        }
    }

    private fun handleResult(result: List<String?>) {
        // Méthode appelée pour traiter le résultat du traitement
        for (i in result.indices) {
            Log.d("RESULT TXT TRAITE", "handleResult: " + result[i])
        }
        val fragTransaction = parentFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.FL, ListeTraitementsFragment())
        fragTransaction.addToBackStack(null)
        fragTransaction.commit()

    }

    private fun startLoadingAnimation() {
        // Méthode appelée pour démarrer l'animation de chargement
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
        // Méthode appelée lors de la destruction du fragment
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    // Fonction qui va lire le texte récupéré depuis l'image et qui fait un traitement après avoir trié les données
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTraitement(text: Uri, context: Context): List<String?> {

        val myModel = PrescriptionAI(requireContext())
        val texteAnalyze = myModel.analyse(text, onPrediction = { prediction ->
            val treatment = mutableListOf(Traitement(
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
            treatment.forEach { it.paufine(context) }
        },
            // retirer le loader
            onDismiss = {})
        return  texteAnalyze
    }
}

