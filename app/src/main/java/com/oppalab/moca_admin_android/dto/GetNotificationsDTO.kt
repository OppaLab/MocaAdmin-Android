package com.oppalab.moca.dto

data class GetNotificationsDTO (
    val page: Int,
    val content: List<NotificationsDTO>,
    val last: Boolean
)