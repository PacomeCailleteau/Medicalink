package dev.mobile.medicalink.fragments.traitements.ajoutmanuel

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mobile.medicalink.R
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.Interaction
import dev.mobile.medicalink.db.local.entity.Medoc
import dev.mobile.medicalink.db.local.repository.CisCompoBdpmRepository
import dev.mobile.medicalink.db.local.repository.InteractionRepository
import dev.mobile.medicalink.db.local.repository.MedocRepository
import dev.mobile.medicalink.db.local.repository.UserRepository
import dev.mobile.medicalink.fragments.traitements.AddTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.ListeTraitementsFragment
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.RecapAdapterR
import dev.mobile.medicalink.fragments.traitements.SpacingRecyclerView
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.text.Normalizer


class AjoutManuelRecapitulatif : Fragment() {

    private lateinit var retour: ImageView
    private lateinit var suivant: Button

    private lateinit var nomMedoc: TextView
    private lateinit var textUnite: TextView
    private lateinit var textStock: TextView
    private lateinit var dateFinDeTraitement: TextView
    private lateinit var sousNomPeriodicite: TextView

    private lateinit var nomLayout: ConstraintLayout
    private lateinit var caracteristiqueLayout: ConstraintLayout
    private lateinit var periodiciteLayout: ConstraintLayout
    private lateinit var priseLayout: ConstraintLayout
    private lateinit var reapprovisionnementLayout: ConstraintLayout


    @RequiresApi(Build.VERSION_CODES.O) //ne marhra pas sous android 8
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_ajout_manuel_recapitulatif, container, false)
        if (activity != null) {
            val navBarre = requireActivity().findViewById<ConstraintLayout>(R.id.fragmentDuBas)
            navBarre.visibility = View.GONE
        }

        retour = view.findViewById(R.id.retour_schema_prise2)
        suivant = view.findViewById(R.id.suivant1)

        val traitement: Traitement? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("traitement", Traitement::class.java)
        } else {
            arguments?.getSerializable("traitement") as Traitement
        }
        if (traitement == null) {
            val destinationFragment = AddTraitementsFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
            return view
        }
        val isAddingTraitement = arguments?.getString("isAddingTraitement")
        val schemaPrise1 = arguments?.getString("schema_prise1")
        val provenance = arguments?.getString("provenance")
        val dureePriseDbt = arguments?.getString("dureePriseDbt")
        val dureePriseFin = arguments?.getString("dureePriseFin")

        val db = AppDatabase.getInstance(view.context.applicationContext)
        val substanceDatabaseInterface = CisCompoBdpmRepository(db.cisCompoBdpmDao())
        val medocDatabaseInterface = MedocRepository(db.medocDao())
        val userDatabaseInterface = UserRepository(db.userDao())
        val interactionDatabaseInterface = InteractionRepository(db.interactionDao())




        nomMedoc = view.findViewById(R.id.nomMedoc)
        textUnite = view.findViewById(R.id.textUnite)
        textStock = view.findViewById(R.id.textStock)
        dateFinDeTraitement = view.findViewById(R.id.dateFinTraitementText)
        sousNomPeriodicite = view.findViewById(R.id.sousNomPeriodicite)

        nomLayout = view.findViewById(R.id.nomLayout)
        caracteristiqueLayout = view.findViewById(R.id.caracteristiqueLayout)
        periodiciteLayout = view.findViewById(R.id.periodiciteLayout)
        priseLayout = view.findViewById(R.id.priseLayout)
        reapprovisionnementLayout = view.findViewById(R.id.reapprovionnementLayout)

        var schemaPriseFormatee = ""
        if (schemaPrise1 != null) {
            when (schemaPrise1) {
                "Quotidiennement" -> {
                    schemaPriseFormatee = "Quotidiennement"
                }

                "Intervalle" -> {
                    schemaPriseFormatee =
                        "Tous les ${traitement.dosageNb} ${traitement.dosageUnite}"
                }

                "auBesoin" -> {
                    schemaPriseFormatee = "Au besoin"
                }
            }
        }

        nomMedoc.text = traitement.nomTraitement
        textUnite.text = traitement.typeComprime
        textStock.text = "${traitement.comprimesRestants} ${traitement.typeComprime}"
        if (traitement.comprimesRestants!! > 1) {
            textStock.text = "${textStock.text}s"
        }
        if (traitement.dateFinTraitement == null) {
            dateFinDeTraitement.text = resources.getString(R.string.indetermine)
        } else {
            dateFinDeTraitement.text =
                "${traitement.dateFinTraitement?.dayOfMonth}/${traitement.dateFinTraitement?.monthValue}/${traitement.dateFinTraitement?.year}"
        }

        sousNomPeriodicite.text = schemaPriseFormatee

        if (schemaPriseFormatee != "Au besoin") {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_recap)
            recyclerView.layoutManager = LinearLayoutManager(context)
            var liste: MutableList<Prise>
            liste = mutableListOf()
            if (traitement.prises != null) {
                liste = traitement.prises!!
            }
            Log.d("test", liste.toString())
            recyclerView.adapter = RecapAdapterR(liste)
            // Gestion de l'espacement entre les éléments du RecyclerView
            val espacementEnDp = 5
            recyclerView.addItemDecoration(SpacingRecyclerView(espacementEnDp))
        } else {
            priseLayout.visibility = View.GONE
        }

        traitement.expire = false
        traitement.effetsSecondaires = null

        suivant.setOnClickListener {
            val bundle = Bundle()

            bundle.putSerializable(
                "newTraitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")

            if (isAddingTraitement == "true" && traitement.CodeCIS != null) {
                checkIfSubstanceDuplicateOrInteraction(
                    traitement.CodeCIS!!,
                    substanceDatabaseInterface,
                    medocDatabaseInterface,
                    userDatabaseInterface,
                    interactionDatabaseInterface
                ) { listDuplicate, listIncompatible, substanceAdd ->

                    //TODO TEST D'autres interactions


                    when {
                        listDuplicate.isNotEmpty() && listIncompatible.isNotEmpty() -> {
                            //les 2 sont remplis
                            activity?.runOnUiThread {
                                this.context?.let { it1 ->
                                    showDuplicateOrInteractionDialog(
                                        it1,
                                        traitement,
                                        (listDuplicate + listIncompatible).distinct(),//peut etre pas le mieux
                                        substanceAdd,
                                        bundle,
                                        "Réagit avec : ",
                                        "Duplications et Interactions Détectées ",
                                        true
                                    )
                                }
                            }
                        }

                        listDuplicate.isNotEmpty() -> {
                            //les duplications sont remplis
                            activity?.runOnUiThread {
                                this.context?.let { it1 ->
                                    showDuplicateOrInteractionDialog(
                                        it1,
                                        traitement,
                                        listDuplicate,
                                        substanceAdd,
                                        bundle,
                                        "A la même substance active que : "
                                    )
                                }
                            }
                        }

                        listIncompatible.isNotEmpty() -> {
                            //les incompatibles sont remplis
                            activity?.runOnUiThread {
                                this.context?.let { it1 ->
                                    showDuplicateOrInteractionDialog(
                                        it1,
                                        traitement,
                                        listIncompatible,
                                        substanceAdd,
                                        bundle,
                                        "Est incompatible avec : ",
                                        "Interactions Détectées"
                                    )
                                }
                            }
                        }

                        else -> {
                            //aucun des deux ne sont remplis
                            val destinationFragment = ListeTraitementsFragment()
                            destinationFragment.arguments = bundle
                            val fragTransaction = parentFragmentManager.beginTransaction()
                            fragTransaction.replace(R.id.FL, destinationFragment)
                            fragTransaction.addToBackStack(null)
                            fragTransaction.commit()
                        }
                    }
                }
            } else {
                //mise a jour
                val destinationFragment = ListeTraitementsFragment()
                destinationFragment.arguments = bundle
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }


        }
        if (isAddingTraitement == "false") {
            val nomLayout = view.findViewById<ConstraintLayout>(R.id.nomLayout)
            nomLayout.isClickable = false
            nomLayout.isFocusable = false
            nomLayout.isEnabled = false
            val fleche = view.findViewById<ImageView>(R.id.imageview40)
            fleche.visibility = View.GONE
        }



        retour.setOnClickListener {

            if (isAddingTraitement == "false") {
                val destinationFragment = ListeTraitementsFragment()
                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
                return@setOnClickListener
            }

            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelStock()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        nomLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSearchFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        caracteristiqueLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelTypeMedic()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }


        periodiciteLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelDateSchemaPrise()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        priseLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelSchemaPrise2Fragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)

            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        reapprovisionnementLayout.setOnClickListener {
            //On appelle le parent pour changer de fragment
            val bundle = Bundle()
            bundle.putSerializable(
                "traitement",
                traitement
            )
            bundle.putString("isAddingTraitement", "$isAddingTraitement")
            bundle.putString("schema_prise1", "$schemaPrise1")
            bundle.putString("provenance", "$provenance")
            bundle.putString("dureePriseDbt", "$dureePriseDbt")
            bundle.putString("dureePriseFin", "$dureePriseFin")
            val destinationFragment = AjoutManuelStock()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDuplicateOrInteractionDialog(
        context: Context,
        traitement: Traitement,
        listTraitements: List<String>,
        substanceAdd: String,
        bundle: Bundle,
        textVue: String,
        titreDialog: String = "Duplications Détectées",
        interactionBool: Boolean = false
    ) {
        val dialog = Dialog(context, R.style.RoundedDialog)
        val dialogView =
            LayoutInflater.from(dialog.context).inflate(R.layout.dialog_duplicate, null)
        dialog.setContentView(dialogView)
        dialog.show()

        val nomMedocAAdd = dialogView.findViewById<TextView>(R.id.medicamentPrincipal)
        val substanceActive = dialogView.findViewById<TextView>(R.id.substanceActive)
        val listeMedoc = dialogView.findViewById<TextView>(R.id.listeMedicaments)
        val okButton = dialogView.findViewById<Button>(R.id.prendreButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.annulerButton)
        val titre = dialogView.findViewById<TextView>(R.id.textView13)

        if (interactionBool) {
            val image = dialogView.findViewById<ImageView>(R.id.imageView4)
            image.setImageResource(R.drawable.interaction)
        }

        titre.text = titreDialog
        nomMedocAAdd.text = getString(R.string.le_m_dicament, traitement.nomTraitement)
        substanceActive.text = getString(R.string.ayant_la_substance_active, substanceAdd)
        listeMedoc.text = buildString {
            append(textVue)
            append("\n- ")
            append(listTraitements.joinToString("\n- "))
        }

        okButton.setOnClickListener {
            val destinationFragment = ListeTraitementsFragment()
            destinationFragment.arguments = bundle
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            val destinationFragment = AddTraitementsFragment()
            val fragTransaction = parentFragmentManager.beginTransaction()
            fragTransaction.replace(R.id.FL, destinationFragment)
            fragTransaction.addToBackStack(null)
            fragTransaction.commit()
            dialog.dismiss()
        }


    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                val traitement = arguments?.getSerializable("traitement") as Traitement
                val isAddingTraitement = arguments?.getString("isAddingTraitement")
                val schemaPrise1 = arguments?.getString("schema_prise1")
                val provenance = arguments?.getString("provenance")
                val dureePriseDbt = arguments?.getString("dureePriseDbt")
                val dureePriseFin = arguments?.getString("dureePriseFin")


                traitement.expire = false
                traitement.effetsSecondaires = null
                traitement.dateFinTraitement = null

                if (isAddingTraitement == "false") {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        "traitement",
                        traitement
                    )
                    bundle.putString("schema_prise1", "$schemaPrise1")
                    bundle.putString("dureePriseDbt", "$dureePriseDbt")
                    bundle.putString("dureePriseFin", "$dureePriseFin")
                    val destinationFragment = ListeTraitementsFragment()
                    destinationFragment.arguments = bundle
                    val fragTransaction = parentFragmentManager.beginTransaction()
                    fragTransaction.replace(R.id.FL, destinationFragment)
                    fragTransaction.addToBackStack(null)
                    fragTransaction.commit()
                    return
                }

                val bundle = Bundle()
                bundle.putSerializable(
                    "traitement",
                    traitement
                )
                bundle.putString("isAddingTraitement", "$isAddingTraitement")
                bundle.putString("schema_prise1", "$schemaPrise1")
                bundle.putString("provenance", "$provenance")
                bundle.putString("dureePriseDbt", "$dureePriseDbt")
                bundle.putString("dureePriseFin", "$dureePriseFin")

                val destinationFragment = AjoutManuelStock()
                destinationFragment.arguments = bundle

                val fragTransaction = parentFragmentManager.beginTransaction()
                fragTransaction.replace(R.id.FL, destinationFragment)
                fragTransaction.addToBackStack(null)
                fragTransaction.commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    /**
     * Récupérer le nom de la substance1 dans cis compo a partir de la clé primaire cis
     * La meme chose pour tous les medoc éxistant
     * liste si substance = a la substance1 et le nom du traitement correspondant
     *
     * @param codeCIS le code cis du traitement a add
     * @param substanceDatabaseInterface la base de donnée des substances
     * @param medocDatabaseInterface la base de donnée des traitements
     * @param userDatabaseInterface la base de donnée des utilisateurs
     * @see String.removeAccents pour enlever les accents
     * @return List<String> liste des nom de traitements contenant la meme substance
     * @return String nom de la substance
     */
    private fun checkIfSubstanceDuplicateOrInteraction(
        codeCIS: Int,
        substanceDatabaseInterface: CisCompoBdpmRepository,
        medocDatabaseInterface: MedocRepository,
        userDatabaseInterface: UserRepository,
        interactionDatabaseInterface: InteractionRepository,
        callback: (List<String>, List<String>, String) -> Unit
    ) {
        Thread {
            //définir les listes résultat
            val listDuplicate: MutableList<String> = mutableListOf()
            val listSubstanceIncompatible: MutableList<String> = mutableListOf()
            val listMedicamentIncompatible: MutableList<String> = mutableListOf()


            val listeMedoc = medocDatabaseInterface.getAllMedocByUserId(
                userDatabaseInterface.getUsersConnected()[0].uuid
            )
            val substanceAdd: String =
                findSubstanceName(codeCIS, substanceDatabaseInterface) ?: return@Thread
            val interactions = mutableListOf<Interaction>()

            //without accents using normalizer JE PREND QUE LE 1ER MOT POUR L'INSTANT SINON TROP DE TRUC
            val premierMotSubstance = substanceAdd.split(" ")[0].removeAccents()

            Log.d("InteractionSubstance", premierMotSubstance)

            interactions += interactionDatabaseInterface.getAllInteractionLikeSubstance(
                premierMotSubstance
            )



            Log.d("InteractionSubstance", interactions.toString())

            findIncompatibles(interactions, listSubstanceIncompatible)

            Log.d("InteractionSubstance", listSubstanceIncompatible.toString())

            for (medoc in listeMedoc) {
                if (medoc.CodeCIS == null) {
                    continue
                }

                val substance =
                    findSubstanceName(medoc.CodeCIS, substanceDatabaseInterface) ?: continue

                //remplir les duplications
                if (substance == substanceAdd) listDuplicate.add(medoc.nom)


                //remplir les incompatibles
                addToIncompatible(
                    listSubstanceIncompatible,
                    substance,
                    listMedicamentIncompatible,
                    medoc
                )


            }

            Handler(Looper.getMainLooper()).post {
                callback(listDuplicate, listMedicamentIncompatible, substanceAdd)
            }
        }.start()


    }

    /**
     * Ajoute les medicaments incompatibles dans la liste
     *
     * amélioration possible "if (element in sub) {
     *   listMedicamentIncompatible.add(medoc.nom)
     *   break@outerLoup // Breaks out of both loops et REMOve l'autre
     *   }" au lieu de if (substance.removeAccents() in element.removeAccents()) {
     *   listMedicamentIncompatible.add(medoc.nom)
     *   } mais pas test
     *
     *
     * @param listSubstanceIncompatible la liste des substances incompatibles
     * @param substance la substance du medoc
     * @param listMedicamentIncompatible la liste des medicaments incompatibles
     * @param medoc le medoc a ajouter
     * @see String.removeAccents pour enlever les accents
     */
    private fun addToIncompatible(
        listSubstanceIncompatible: List<String>,
        substance: String,
        listMedicamentIncompatible: MutableList<String>,
        medoc: Medoc
    ) {
        outerLoup@
        //pour chaque substance incompatible avec le medoc ajouté
        for (element in listSubstanceIncompatible) {
            //pour chaque mot de la substance du medoc
            for (sub in substance.split(" ")) {
                //si le mot est dans la substance incompatible
                if (sub in element) {
                    listMedicamentIncompatible.add(medoc.nom)
                    break@outerLoup // Breaks out of both loops
                }
            }
            //si la substance incompatible est dans la substance du medoc
            if (substance.removeAccents() in element.removeAccents()) {
                listMedicamentIncompatible.add(medoc.nom)
            }
        }
    }

    private fun findIncompatibles(
        interactions: List<Interaction>,
        listSubstanceIncompatible: MutableList<String>
    ) {
        if (interactions.isEmpty()) return
        for (interaction in interactions) {
            listSubstanceIncompatible.addAll(interaction.incompatibles.split(";"))
        }
    }

    /**
     * Enleve les accents d'une chaine de caractère
     * \\p enléve les espace pas sur que ce soit bon
     * @return String la chaine de caractère sans accents
     */
    private fun String.removeAccents(): String {
        val regex = "[^\\p{ASCII}]"
        return Normalizer.normalize(this, Normalizer.Form.NFD).replace(regex.toRegex(), "")
    }

    //try to find the substance name in compo
    private fun findSubstanceName(
        codeCIS: Int,
        substanceDatabaseInterface: CisCompoBdpmRepository
    ): String? {
        return try {
            //[0 car c'est des listes]
            substanceDatabaseInterface.getOneCisCompoBdpmById(codeCIS)[0].denomination
        } catch (e: Exception) {
            null
        }
    }


}
