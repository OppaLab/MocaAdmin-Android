package com.oppalab.moca_admin_android.dto

data class ReportDTO (
        val reportId: Long,
        val userId: Long,
        val userNickName: String,
        val reportedUserId: Long,
        val reportedUserNickName: String,
        val reportWhat: String,
        val postId: Long,
        val reviewId: Long,
        val commentId: Long,
        val reportReason: String,
        val createdAt: Long,
)