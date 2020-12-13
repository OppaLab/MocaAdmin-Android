package com.oppalab.moca_admin_android.dto

data class GetReviewDTO (
    val reviewId: Long,
    val likeCount: Long,
    val commentCount: Long,
    val like: Boolean,
    val userId: Long,
    val nickname: String,
    val review: String,
    val profileImageFilePath: String,
    val createdAt: Long,
)