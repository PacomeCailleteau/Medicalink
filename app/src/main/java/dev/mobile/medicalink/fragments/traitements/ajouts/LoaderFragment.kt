package dev.mobile.medicalink.fragments.traitements.ajouts

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.ListeTraitementsFragment
import dev.mobile.medicalink.utils.GoTo
import dev.mobile.medicalink.utils.ModelOCR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoaderFragment : Fragment() {

    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loader, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLoadingAnimation()

        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                createTraitement("Votre texte à traiter")
            }

            handleResult(result)
        }
    }

    private fun handleResult(result: List<String?>) {
        for (i in result.indices) {
            Log.d("RESULT TXT TRAITE", "handleResult: " + result[i])
        }
        GoTo.fragment(ListeTraitementsFragment(), parentFragmentManager)
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

    // Fonction qui va lire le texte récupéré depuis l'image et qui fait un traitement après avoir trié les données

    private fun createTraitement(text: String): List<String?> {
        val myModel = ModelOCR(requireContext())
        val texteAnalyze = myModel.analyze(text)
        return texteAnalyze
    }
}

