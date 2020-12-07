package com.oppalab.moca_admin_android.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.OtherUserActivity
import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.R
import com.oppalab.moca.ReviewActivity
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.GetReviewDTO
import com.oppalab.moca.dto.NotificationsDTO
import com.oppalab.moca.dto.PostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.oppalab.moca_admin_android.R
import com.oppalab.moca_admin_android.dto.NotificationsDTO
import com.oppalab.moca_admin_android.dto.PostDTO
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter (
    private val mContext: Context,
    private val mNotification: List<NotificationsDTO>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{

    private var currentUser = PreferenceManager.getLong(mContext,"userId")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotification[position]

        val createdAt = notification.createdAt
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



        //썸네일을 넣을것인지 않넣을것인지 안정해서 일단 없앰
        holder.postThumbnail.visibility= View.GONE
        holder.profileImage.setImageResource(R.drawable.profile)
        if(notification.activity == "post")
        {
            holder.username.text= notification.nickname
            holder.text.text="새로운 고민글을 작성했어요. "
            holder.textTime.text = createdAtToText

            holder.text.setOnClickListener {
                moveToPost(notification.userId, notification.postId)
            }
        }
        else if(notification.activity == "review")
        {
            holder.username.text= notification.nickname
            holder.text.text="관심있었던 고민글에 후기글이 등록됐어요. "
            holder.textTime.text = createdAtToText

            holder.text.setOnClickListener {
                moveToReview(notification.userId.toString(), notification.reviewId.toString(), notification.postId)
            }
        }
        else if(notification.activity == "like")
        {
            holder.username.text= notification.nickname
            if(notification.reviewId == 0L)
            {
                holder.text.text="고민글에 공감했어요. "
                holder.textTime.text = createdAtToText
                holder.text.setOnClickListener {

                    moveToPost(currentUser, notification.postId)
                }
            }
            else
            {
                holder.text.text="후기에 공감했어요. "
                holder.textTime.text = createdAtToText
                holder.text.setOnClickListener {
                    moveToReview(currentUser.toString(), notification.reviewId.toString(), notification.postId)
                }
            }
        }
        else if(notification.activity == "follow")
        {
            holder.username.text= notification.nickname
            holder.text.text="당신을 팔로우합니다. "
            holder.textTime.text = createdAtToText
            holder.text.setOnClickListener{
                moveToProfile(notification.userId)
            }
        }
        else if(notification.activity == "comment")
        {
            holder.username.text= notification.nickname
            if(notification.reviewId == 0L)
            {
                Log.d("notificationcomment", currentUser.toString()+notification.postId.toString())
                holder.text.text="고민글에 댓글이 등록됐어요. "
                holder.textTime.text = createdAtToText
                holder.text.setOnClickListener {
                    moveToPost(currentUser, notification.postId)
                }
            }
            else
            {
                holder.text.text="후기에 댓글이 등록됐어요. "
                holder.textTime.text = createdAtToText
                holder.text.setOnClickListener {
                    moveToReview(currentUser.toString(), notification.reviewId.toString(), notification.postId)
                }
            }
        }
        else
        {
            holder.username.text= notification.nickname
            holder.text.text= notification.nickname + " 유저가 고민을 푸시했습니다. "
            holder.textTime.text = createdAtToText
            holder.text.setOnClickListener {
                moveToPost(notification.userId, notification.postId)
            }

        }

    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postThumbnail: ImageView
        var profileImage: CircleImageView
        var username: TextView
        var text: TextView
        var textTime: TextView

        init{
            postThumbnail = itemView.findViewById(R.id.notification_post_thumnail)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            username = itemView.findViewById(R.id.username_notification)
            text = itemView.findViewById(R.id.content_notification)
            textTime = itemView.findViewById(R.id.time_notification)
        }
    }

    private fun moveToPost(userId: Long, postId : Long)
    {
        RetrofitConnection.server.getOnePost(userId = userId, postId = postId ,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("moveToPost", response.body().toString())
                val intentPostDetail = Intent(mContext, PostDetailActivity::class.java)

                var mPost : MutableList<PostDTO> = ArrayList()

                mPost.add(response.body()!!.content[0])

                intentPostDetail.putExtra("postId", mPost[0].postId.toString())

                mContext.startActivityㅎ(intentPostDetail)
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("moveToPost", t.message.toString())
            }
        })
    }


}