package com.oppalab.moca_admin_android.dto


data class GetReportDTO (
    val page: Int,
    val content: List<ReportDTO>,
    val last: Boolean
)