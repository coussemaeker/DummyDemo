package com.ddrcss.android.dummydemo.rest_service

import com.ddrcss.android.dummydemo.Constant
import com.ddrcss.android.dummydemo.model.UserFullInfo
import com.ddrcss.android.dummydemo.model.UsersPage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyIoService {

    @GET("user")
    fun getUserList(
        @Header(HEADER_APP_ID) appId: String,
        @Query(QUERY_PAGE_INDEX) page: Int = Constant.DEFAULT_PAGE_INDEX,
        @Query(QUERY_LIMIT) limit: Int = Constant.DEFAULT_PAGE_LIMIT
    ): Call<UsersPage>

    @GET("user/{userId}")
    fun getUser(@Header(HEADER_APP_ID) appId: String,
                @Path(SEGMENT_USER_ID) userId: String): Call<UserFullInfo>

    companion object {
        private const val HEADER_APP_ID = "app-id"
        private const val QUERY_LIMIT = "limit"
        private const val QUERY_PAGE_INDEX = "page"
        private const val SEGMENT_USER_ID = "userId"
    }
}