package com.oppalab.moca_admin_android

import GetCommentsOnPostDTO
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca_admin_android.adapter.CommentsAdapterRetro
import com.oppalab.moca_admin_android.dto.CommentsOnPost
import com.oppalab.moca_admin_android.dto.GetReviewDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {

    private var currentUser = 0L
    private var postId = ""
    private var userId = ""
    private var reviewId = ""
    private var commentAdapter: CommentsAdapterRetro? = null
    private var commentList: MutableList<CommentsOnPost>? = null
    private var postTitle = ""
    private var thumbNailImageFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setSupportActionBar(findViewById(R.id.review_toolbar))


        val intent = intent
        currentUser = PreferenceManager.getLong(applicationContext, "userId")
        postId = intent.getStringExtra("postId")!!
        userId = intent.getStringExtra("userId")!!
        reviewId = intent.getStringExtra("reviewId")!!
        postTitle = intent.getStringExtra("postTitle")!!
        thumbNailImageFilePath = intent.getStringExtra("thumbNailImageFilePath")!!

        review_detail_subject.text = postTitle

        Picasso.get().load(RetrofitConnection.URL + "/image/thumbnail/" + thumbNailImageFilePath)
            .into(review_thumbnail_detail)


        var comment_linearLayoutManager = LinearLayoutManager(this)
        comment_linearLayoutManager.reverseLayout = true
        review_recycler_view_comments.layoutManager = comment_linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentsAdapterRetro(this, commentList)
        review_recycler_view_comments.adapter = commentAdapter


        RetrofitConnection.server.getReview(userId = userId, reviewId = reviewId).enqueue(object:
            Callback<GetReviewDTO> {
            override fun onResponse(
                call: Call<GetReviewDTO>,
                response: Response<GetReviewDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                review_text.text = response.body()!!.review
                review_publisher.text = response.body()!!.nickname
            }

            override fun onFailure(call: Call<GetReviewDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })


        RetrofitConnection.server.getCommentOnPost(postId = "", reviewId = reviewId, page = 0).enqueue(object:
            Callback<GetCommentsOnPostDTO> {
            override fun onResponse(
                call: Call<GetCommentsOnPostDTO>,
                response: Response<GetCommentsOnPostDTO>
            ) {
                Log.d("retrofit", response.body().toString())

                commentList!!.clear()
                for (comment in response.body()!!.content) {
                    val curComment = comment
                    commentList!!.add(curComment!!)
                }

                commentAdapter!!.notifyDataSetChanged()
                review_comments_count.text = response.body()!!.content.size.toString() + "개의 댓글이 있습니다."

            }

            override fun onFailure(call: Call<GetCommentsOnPostDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })
        //Swipe 추가
        val swipe = object: CommentsAdapterRetro.SwipeHelper(this, review_recycler_view_comments, 200) {
            override fun instantiateDeleteButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<DeleteButton>
            ) {
                buffer.add(
                    DeleteButton(this@ReviewActivity, "Delete", 30, 0,
                        Color.parseColor("#FF3C30"),
                        object:CommentsAdapterRetro.CommentClickListener{
                            override fun onClick(pos: Int) {
                                    RetrofitConnection.server.deleteComment(
                                        commentId = commentList!![pos].commentId,
                                        userId = commentList!![pos].userId
                                    ).enqueue(object : Callback<Long> {
                                        override fun onResponse(
                                            call: Call<Long>,
                                            response: Response<Long>
                                        ) {
                                            Log.d(
                                                "retrofit",
                                                "Comment 삭제 : comment_id = " + response.body()
                                            )
                                            commentAdapter!!.notifyDataSetChanged()
                                            finish()
                                            startActivity(intent)
                                        }

                                        override fun onFailure(call: Call<Long>, t: Throwable) {
                                            Log.d("retrofit", "Comment 삭제 실패" + t.message.toString())
                                        }
                                    })
                            }
                        })
                )
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
            menuInflater.inflate(R.menu.appbar_action, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                Log.d("retrofit", "review 삭제버튼 동작 = ")
                RetrofitConnection.server.deleteReview(userId = currentUser, reviewId = reviewId.toLong()).enqueue(object : Callback<Long>{
                    override fun onResponse(call: Call<Long>, response: Response<Long>) {
                        Log.d("retrofit", "review 삭제 : review_id = " + response.body())
                        val intent = Intent(this@ReviewActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    override fun onFailure(call: Call<Long>, t: Throwable) {
                        Log.d("retrofit", "review 삭제 실패 :  " + t.message.toString())
                    }
                })
                return true
            }
            R.id.action_ben -> {
                Log.d("retrofit", "유저 계정 삭제 ")
                val mainIntent = Intent (this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        RetrofitConnection.server.getReview(userId = userId, reviewId = reviewId).enqueue(object:
            Callback<GetReviewDTO> {
            override fun onResponse(call: Call<GetReviewDTO>, response: Response<GetReviewDTO>) {
            }

            override fun onFailure(call: Call<GetReviewDTO>, t: Throwable) {
                Log.d("Fail to get review", t.message.toString())
            }
        })
    }
}