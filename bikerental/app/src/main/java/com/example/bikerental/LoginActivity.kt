package com.example.bikerental

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.bikerental.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)

        user = Firebase.auth.currentUser!!
        if (user != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           startActivity(intent)
           finish()
        }

        binding.btnRegisterInstead.setOnClickListener {
            val switchMethodIntent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(switchMethodIntent)
        }

        binding.login.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.email.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter an email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.password.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter a password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = binding.email.text.toString().trim{ it <= ' '}
                    val password: String = binding.password.text.toString().trim{ it <= ' '}

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        // when account has been made, set it
                        .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Your account has been made.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    // remove all 'layers' created by switching login/register intent

                                    val db = FirebaseFirestore.getInstance()
                                    val result = db.collection("user").document(userId)
                                        .get()

                                    Log.d("User profile", result.toString())

                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", userId)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                }
            }

        }

        setContentView(binding.root)
    }
}