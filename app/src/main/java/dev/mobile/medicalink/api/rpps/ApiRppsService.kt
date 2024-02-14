package dev.mobile.medicalink.api.rpps


import android.util.Log
import dev.mobile.medicalink.db.local.entity.Contact
import retrofit2.Response
import retrofit2.http.*

interface ApiRppsService {
    @GET("practicians/{search}")
    suspend fun getPracticians(@Path("search") search: String): Response<List<Practician>>

    @GET("getEmails/{rpps}")
    suspend fun getEmail(@Path("rpps") rpps: Long): Response<String>
}
