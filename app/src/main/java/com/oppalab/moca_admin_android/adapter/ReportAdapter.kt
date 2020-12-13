package com.oppalab.moca_admin_android.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca_admin_android.PostDetailActivity
import com.oppalab.moca_admin_android.R
import com.oppalab.moca_admin_android.ReviewActivity
import com.oppalab.moca_admin_android.dto.ReportDTO
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import kotlin.collections.ArrayList

class ReportAdapter(
        private val mContext: Context,
        private val mReport: ArrayList<ReportDTO>) :
    RecyclerView.Adapter<ReportAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.reports_item_layout, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = mReport[position]

        holder.reason.text = "신고사유:  " + report.reportReason
        val createdAt = report.createdAt
        var createdAtToText = ""
        if (createdAt <= 60){
            createdAtToText = createdAt.toString() + "초 전"
        } else if (createdAt <= 60*60){
            createdAtToText = (createdAt/60).toString() + "분 전"
        } else if (createdAt <= 60*60*24){
            createdAtToText = (createdAt/(60*60)).toString() + "시간 전"
        } else if (createdAt <= 60*60*24*7){
            createdAtToText = (createdAt/(60*60*7)).toString() + "일 전"
        } else if (createdAt <= 60*60*24*7*7){
            createdAtToText = (createdAt/(60*60*7*7)).toString() + "주 전"
        }


        holder.profileImage.setImageResource(R.drawable.profile)
        if(report.reportWhat == "post")
        {
            holder.username.text= report.userNickName
            holder.text.text=report.reportedUserNickName+"님의 고민글을 신고했습니다."
            holder.textTime.text = createdAtToText

            holder.text.setOnClickListener {
                moveToPost(report.reportedUserId, report.postId, 0L)
            }
        }
        else if(report.reportWhat == "review")
        {
            holder.username.text= report.userNickName
            holder.text.text=report.reportedUserNickName+"님의 후기글을 신고했습니다. "
            holder.textTime.text = createdAtToText

            holder.text.setOnClickListener {
                moveToReview(report.reportedUserId.toString(), report.reviewId.toString(), report.postId, 0L)
            }
        }
        else
        {
            holder.username.text= report.userNickName
            holder.text.text= report.reportedUserNickName + "님의 댓글을 신고했습니다."
            holder.textTime.text = createdAtToText
            if(report.reviewId == 0L)
            {
                holder.text.setOnClickListener {
                    moveToPost(report.userId, report.postId, report.commentId)
                }
            }
            else
            {
                holder.text.setOnClickListener {
                    moveToReview(report.reportedUserId.toString(), report.reviewId.toString(), report.postId, report.commentId)
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return mReport.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var username: TextView
        var text: TextView
        var textTime: TextView
        var reason : TextView

        init{
            profileImage = itemView.findViewById(R.id.report_profile_image)
            username = itemView.findViewById(R.id.username_report)
            text = itemView.findViewById(R.id.content_report)
            textTime = itemView.findViewById(R.id.time_report)
            reason = itemView.findViewById(R.id.reason_report)
        }
    }

    private fun moveToPost(userId: Long, postId : Long, commentId: Long)
    {
        val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)
        if(commentId != 0L) {
            intentPostDetail.putExtra("commentId", commentId)
            intentPostDetail.putExtra("flag",true)
        }
        else {
            intentPostDetail.putExtra("flag", false)
        }
            intentPostDetail.putExtra("postId", postId)
        intentPostDetail.putExtra("postUserId", userId)
        mContext.startActivity(intentPostDetail.addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    private fun moveToReview(userId: String, reviewId: String, postId: Long, commentId : Long)
    {
        val intentReview = Intent(mContext, ReviewActivity::class.java)
        if(commentId != 0L) {
            intentReview.putExtra("commentId", commentId)
            intentReview.putExtra("flag",true)
        }
        else {
            intentReview.putExtra("flag", false)
        }
        intentReview.putExtra("userId",userId)
        intentReview.putExtra("postId",postId)
        intentReview.putExtra("reviewId",reviewId)
        intentReview.putExtra("flag",false)
        mContext.startActivity(intentReview.addFlags(FLAG_ACTIVITY_NEW_TASK))
    }
}