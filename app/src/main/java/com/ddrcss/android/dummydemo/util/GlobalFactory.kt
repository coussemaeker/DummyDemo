package com.ddrcss.android.dummydemo.util

import android.annotation.SuppressLint
import android.content.Context
import com.ddrcss.android.dummydemo.Constant
import com.ddrcss.android.dummydemo.cache.FileCache
import com.ddrcss.android.dummydemo.rest_service.DummyIoService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("StaticFieldLeak")
object GlobalFactory {

    lateinit var context: Context

    val gson: Gson = GsonBuilder().serializeNulls().create()

    val okhttp: OkHttpClient = OkHttpClient.Builder().build()

    val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constant.DUMMY_IO_BASE).client(okhttp)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()

    val dummyIoService: DummyIoService = retrofit.create(DummyIoService::class.java)

    val cache : FileCache by lazy { FileCache(context, gson) }
}