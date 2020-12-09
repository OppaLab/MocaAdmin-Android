package com.oppalab.moca_admin_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca_admin_android.dto.GetReportDTO
import com.oppalab.moca.util.RetrofitConnection
import com.oppalab.moca_admin_android.adapter.ReportAdapter
import com.oppalab.moca_admin_android.dto.ReportDTO
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var reportList : MutableList<ReportDTO>? = null
    private var reportAdapter: ReportAdapter? = null
    private var curPage: Long = 0L
    private var currentUser: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var recyclerView: RecyclerView = findViewById(R.id.report_recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)


        reportList = ArrayList()


        reportAdapter = applicationContext?.let { ReportAdapter(it, reportList as ArrayList<ReportDTO>) }
        recyclerView.adapter = reportAdapter
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount -1
                if (lastVisibleItemPosition == itemTotalCount) {
                    curPage += 1
                    report_progress_bar.visibility = View.VISIBLE
                    readReportsMore()
                }
            }
        })

        readReports()
    }

    private fun readReports() {

        RetrofitConnection.server.getReport(
            page = curPage
        ).enqueue(
            object : Callback<GetReportDTO> {
                override fun onResponse(
                        call: Call<GetReportDTO>,
                        response: Response<GetReportDTO>
                ) {
                    Log.d("Notifications", response.body().toString())

                    reportList?.clear()

                    for (notification in response.body()!!.content) {
                        val curReport = notification
                        if(curReport.userId.toString() != currentUser.toString()) {
                            reportList!!.add(curReport)
                        }
                        reportAdapter!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last == true) {
                        Toast.makeText(applicationContext, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetReportDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }

    private fun readReportsMore() {

        RetrofitConnection.server.getReport(
            page = curPage
        ).enqueue(
            object : Callback<GetReportDTO> {
                override fun onResponse(
                        call: Call<GetReportDTO>,
                        response: Response<GetReportDTO>
                ) {
                    Log.d("Notifications", response.body().toString())

                    for (report in response.body()!!.content) {
                        val curReport = report
                        if(curReport.userId.toString() != currentUser.toString()) {
                            reportList!!.add(curReport)
                        }
                        reportAdapter!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last == true) {
                        Toast.makeText(applicationContext, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetReportDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }
}