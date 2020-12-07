package com.oppalab.moca_admin_android.dto

data class CommentsOnPost(
    val userId: Long,
    val nickname: String,
    val comment: String,
    val createAt: Long,
    val commentId: Long
)