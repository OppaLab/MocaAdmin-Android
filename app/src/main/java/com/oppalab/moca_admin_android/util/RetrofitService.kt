package com.oppalab.moca.util

import GetCommentsOnPostDTO
import com.oppalab.moca_admin_android.dto.GetMyPostDTO
import com.oppalab.moca_admin_android.dto.GetReportDTO
import com.oppalab.moca_admin_android.dto.GetReviewDTO
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //user


    @DELETE("/deleteAccountByAdmin")
    fun deleteAccountByAdmin(
        @Query("userId") userId: Long
    ): Call<Void>

    //post

    @GET("/post")
    fun getOnePost(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long,
        @Query("search") search: String,
        @Query("category") category: String,
        @Query("page") page: Long
    ): Call<GetMyPostDTO>


    @DELETE("/deleteReviewByAdmin")
    fun deleteReview(
        @Query("reviewId") reviewId: Long
    ): Call<Long>

    @DELETE("/deletePostByAdmin")
    fun deletePost(
        @Query("postId") postId: Long
    ): Call<Long>

    //notifications
    @GET("/report")
    fun getReport(
        @Query("page") page: Long
    ): Call<GetReportDTO>

    @DELETE("/deleteCommentByAdmin")
    fun deleteComment(
        @Query ("commentId") commentId: Long
    ): Call<Long>

    @GET("/review")
    fun getReview(
        @Query ("userId") userId: String,
        @Query ("reviewId") reviewId: String
    ): Call<GetReviewDTO>


    @GET("/comment")
    fun getCommentOnPost(
        @Query("postId") postId: String,
        @Query("reviewId") reviewId: String,
        @Query("page") page: Long
    ): Call<GetCommentsOnPostDTO>


}