package com.oppalab.moca_admin_android

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.oppalab.moca_admin_android.adapter.NotificationAdapter
import com.oppalab.moca_admin_android.dto.NotificationsDTO

class MainActivity : AppCompatActivity() {

    private var notificationList : MutableList<NotificationsDTO>? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var curPage: Long = 0L
    private var currentUser: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


}