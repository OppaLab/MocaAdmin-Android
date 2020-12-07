package com.oppalab.moca_admin_android.dto

data class NotificationsDTO (
    val userId: Long,
    val nickname: String,
    val profileImageFilePath: String,
    val postId: Long,
    val reviewId: Long,
    val activity: String,
    val comment: String,
    val createdAt: Long
)