package com.oppalab.moca_admin_android

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.oppalab.moca_admin_android.adapter.CommentsAdapterRetro
import com.oppalab.moca_admin_android.dto.CommentsOnPost
import com.oppalab.moca_admin_android.dto.PostDTO
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailActivity : AppCompatActivity() {
    private var postId = 0L
    private var postUserId = ""
    private var reviewId = ""
    private var publisherId = ""
    private var content = ""
    private var thumbnailImageFilePath = ""
    private var subject = ""
    private var createdAt = 0L
    private var categories = ""
    private var commentCount = 0L
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        setSupportActionBar(findViewById(R.id.post_toolbar))




        val intent = intent

        postId = intent.getStringExtra("postId")!!.toLong()

        RetrofitConnection.server.getOnePost(
            userId = postUserId.toLong(),
            postId = postId,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                val mPost = response.body()!!.content[0]

                postUserId = mPost.userId.toString()
                publisherId = mPost.nickname
                reviewId = mPost!!.reviewId.toString()
                subject = mPost.postTitle
                thumbnailImageFilePath = mPost.thumbnailImageFilePath
                content = mPost.postBody
                createdAt = mPost.createdAt
                categories = mPost.categories.toString()

                Picasso.get()
                    .load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath)
                    .into(post_thumbnail_detail)

                var post_categories = ""
                for (category in mPost.categories) {
                    if (category == "") continue
                    post_categories += category
                    post_categories += ", "
                }
                post_detail_cateogory.text =
                    post_categories.substring(0, post_categories.length - 2)

                post_detail_subject.text = subject
                post_detail_publisher.text = "\"" + publisherId + "\"님이 작성해주신 글이에요."
                if (createdAt <= 60) {
                    post_detail_time.text = createdAt.toString() + "초 전에 작성된 글입니다."
                } else if (createdAt <= 60 * 60) {
                    post_detail_time.text = (createdAt / 60).toString() + "분 전에 작성된 글입니다."
                } else if (createdAt <= 60 * 60 * 24) {
                    post_detail_time.text = (createdAt / (60 * 60)).toString() + "시간 전에 작성된 글입니다."
                } else if (createdAt <= 60 * 60 * 24 * 7) {
                    post_detail_time.text =
                        (createdAt / (60 * 60 * 7)).toString() + "일 전에 작성된 글입니다."
                } else if (createdAt <= 60 * 60 * 24 * 7 * 7) {
                    post_detail_time.text =
                        (createdAt / (60 * 60 * 7 * 7)).toString() + "주 전에 작성된 글입니다."
                }
                post_text.text = content

            }
            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        post_detail_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        post_detail_recycler_view_comments.adapter = commentAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
           menuInflater.inflate(R.menu.appbar_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.action_delete -> {
                Log.d("retrofit", "유저 벤 시작")
                RetrofitConnection.server.deletePost(postId = postId, userId = postUserId.toLong()).enqueue(object : Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "post 삭제 : post_id = " + response.body())
                        Toast.makeText(this@PostDetailActivity,"게시글이 삭제되었습니다.", Toast.LENGTH_LONG)
                        val intent = Intent(this@PostDetailActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", "post 삭제 실패 :  " + t.message.toString())
                    }
                })
                return true
            }
            R.id.action_ben -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {
        super.onResume()
        setSupportActionBar(findViewById(R.id.post_toolbar))
        RetrofitConnection.server.getOnePost(userId = postUserId.toLong(), postId = postId ,search = "", category = "", page = 0).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {

                val mPost : MutableList<PostDTO> = ArrayList()
                mPost.add(response.body()!!.content[0])

            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("moveToPost", t.message.toString())
            }
        })
    }
}