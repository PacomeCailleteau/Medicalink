package dev.mobile.medicalink.fragments.traitements

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
                createTraitement(uri!!)
            }

            handleResult(result)
        }
    }

    private fun handleResult(result: List<Traitement>) {
        // Création d'un bundle pour transmettre les résultats au nouveau fragment
        val bundle = Bundle().apply {
            putSerializable("result", ArrayList(result))
        }

        // Création d'une instance du nouveau fragment
        val listeTraitementsFragment = ListeTraitementsFragment().apply {
            arguments = bundle
        }

        // Remplacement du fragment actuel par le nouveau fragment
        val fragTransaction = parentFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.FL, listeTraitementsFragment)
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
    private fun createTraitement(text: Uri): List<Traitement> {
        val myModel = PrescriptionAI(requireContext())
        return myModel.analyse(text)
    }
}

