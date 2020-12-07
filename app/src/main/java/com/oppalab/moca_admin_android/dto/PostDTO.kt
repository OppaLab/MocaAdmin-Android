package com.oppalab.moca_admin_android.dto

data class PostDTO (
    val likeCount: Long,
    val commentCount: Long,
    val like: Boolean,
    val postId: Long,
    val userId: Long,
    val nickname: String,
    val profileImageFilePath: String,
    val postTitle: String,
    val postBody: String,
    val thumbnailImageFilePath: String,
    val createdAt: Long,
    val categories: List<String>,
    val reviewId: Long
)