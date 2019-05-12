package com.example.crop

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitSettings {

    companion object {
        fun createApi () : WebApi {
            val loggingInterceptor: HttpLoggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://hereIsURL/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(WebApi::class.java)
        }
    }

}

