package com.example.crop

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface WebApi {

@Multipart
@PUT("endpointHere")
fun sendPhoto(@Part photo:MultipartBody.Part) : Deferred<Response<String>>
}