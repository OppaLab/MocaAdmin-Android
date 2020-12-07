package com.oppalab.moca.util

import com.oppalab.moca.dto.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //user

    @FormUrlEncoded
    @POST("/signin")
    fun signIn(
        @Field("email") email: String
    ): Call<Long>


    @DELETE("/signout/{userId}")
    fun signOut(
        @Path("userId") userId: Long
    ): Call<Long>


    //post

    @GET("/post")
    fun getOnePost(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long,
        @Query("search") search: String,
        @Query("category") category: String,
        @Query("page") page: Long
    ): Call<GetMyPostDTO>


    @DELETE("/review")
    fun deleteReview(
        @Query("reviewId") reviewId: Long,
        @Query("userId") userId: Long
    ): Call<Long>

    @DELETE("/post")
    fun deletePost(
        @Query("postId") postId: Long,
        @Query("userId") userId: Long
    ): Call<Long>

    @FormUrlEncoded
    @POST("/review")
    fun createReview(
        @Field ("postId") postId : Long,
        @Field ("userId") userId : Long,
        @Field ("review") review : String
    ): Call<Long>


    //notifications
    @GET("/activity")
    fun getNotifications(
        @Query("userId") userId: Long,
        @Query("page") page: Long
    ): Call<GetNotificationsDTO>

}