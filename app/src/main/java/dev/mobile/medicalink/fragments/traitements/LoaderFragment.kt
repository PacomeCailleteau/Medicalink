package dev.mobile.medicalink.fragments.traitements

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import dev.mobile.medicalink.R
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // On démare l'animation
        startLoadingAnimation()

        // On fait une coroutine pour traiter le texte
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                // Appel de votre fonction ici (par exemple, createTraitement)
                createTraitement("Votre texte à traiter")
            }

            // Traitez le résultat ici
            handleResult(result)
        }
    }

    private fun handleResult(result: List<String?>) {
        // Une fois qu"on a le résulatat, on peut l'afficher
        for (i in result.indices) {
            Log.d("RESULT TXT TRAITE", "handleResult: " + result[i])
        }
        //On peut ensuite changer de fragment
        val fragTransaction = parentFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.FL, ListeTraitementsFragment())
        fragTransaction.addToBackStack(null)
        fragTransaction.commit()

    }

    private fun startLoadingAnimation() {
        val loaderProgressBar = requireView().findViewById<View>(R.id.loaderProgressBar)
        // Créez une animation d'opacité avec une durée de 500 ms
        val fadeInOut = ObjectAnimator.ofFloat(loaderProgressBar, "alpha", 0f, 1f).apply {
            duration = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Utilisez un Handler pour déclencher l'animation après un délai de 500 ms
        handler.postDelayed({
            // Affichez la ProgressBar et démarrez l'animation
            fadeInOut.start()
        }, 500)
    }

    override fun onDestroy() {
        // Assurez-vous de supprimer le callback du Handler pour éviter des fuites de mémoire
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    // Fonction qui va lire le texte récupéré depuis l'image et en faire un traitement après avoir trié les données
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTraitement(text: String): List<String?> {
        val myModel = ModelOCR(requireContext())
        val texteAnalyze = myModel.analyze(text)
        return texteAnalyze
    }
}

