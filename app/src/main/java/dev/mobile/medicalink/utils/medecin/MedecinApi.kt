package dev.mobile.medicalink.utils.medecin


import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

/**
 * Classe permettant de gérer les appels à l'API pour les médecins
 */
class MedecinApi {

    private val baseURL = "https://medica-zelda-api.vercel.app/"

    fun getMedecin(rpps: String): Medecin? {
        try {
            val url = "${baseURL}medecin/$rpps"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            println(url)
            val response: Response = client.newCall(request).execute()
            println(" $rpps")
            if (!response.isSuccessful) {
                return null
            }

            val body = response.body?.string()
            val jsonBody = body?.let { JSONObject(it) }
            val gson = Gson()
            return gson.fromJson(jsonBody.toString(), Medecin::class.java)

        } catch (e: Exception) {
            return null
        }
    }

    fun getMedecins(prenom: String?, nom: String?) : List<Medecin>? {
        try {
            // On ne cherche qu'avec instamed parce que c'est plus rapide
            val url = "${baseURL}medecin_instamed/$prenom/$nom?perPage=10"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                // Gérer les erreurs HTTP ici
                return null
            }

            val body = response.body?.string()
            val gson = Gson()

            return gson.fromJson(body, Array<Medecin>::class.java).toList()

        } catch (e: Exception) {
            return null
        }
    }


}
