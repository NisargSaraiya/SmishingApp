package com.example.smishingapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


const val url = "https://smishing-service.onrender.com"

data class UserModel(
    var score: List<List<Float> >
)

data class UserInput(
    var message: String
)

var okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(url)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface UserApi {
    @Headers(
        "Accept: application/json"
    )
    @POST("api")
    suspend fun getScore(@Body text:UserInput): UserModel
}

object Api {
    val retrofitService: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}