package dev.mobile.medicalink.api.rpps


import retrofit2.Response
import retrofit2.http.*

interface ApiRppsService {
    @GET("practician/{search}")
    suspend fun getPractician(@Path("search") search: String): Response<List<Practician>>
}
