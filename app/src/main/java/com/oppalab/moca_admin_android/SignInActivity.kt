package com.oppalab.moca_admin_android

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)



        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = login_email.text.toString()
        val password = login_password.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "이메일 입력이 필요합니다.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "비밀번호 입력이 필요합니다.", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("로그인")
                progressDialog.setMessage("잠시만 기다려 주십시오.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressDialog.dismiss()
                        RetrofitConnection.server.signIn(email = email).enqueue(object: Callback<Long> {
                            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                                Log.d("signInUserId", response.body().toString())
                                PreferenceManager.setLong(applicationContext, "userId", response.body()!!.toLong())
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                            override fun onFailure(call: Call<Long>, t: Throwable) {
                                Log.d("retrofit", t.message.toString())
                            }

                        })
                    } else {
                        val message = it.exception!!.toString()
                        Toast.makeText(this, "Error $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            RetrofitConnection.server.signIn(email = FirebaseAuth.getInstance().currentUser!!.email.toString()).enqueue(object: Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    Log.d("signInUserId", response.body().toString())
                    PreferenceManager.setLong(applicationContext, "userId", response.body()!!.toLong())
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
    }
}