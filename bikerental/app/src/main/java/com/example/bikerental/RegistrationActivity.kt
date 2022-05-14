package com.example.bikerental

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bikerental.databinding.ActivityRegistrationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class RegistrationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)

        binding.btnLoginInstead.setOnClickListener {
            val switchMethodIntent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(switchMethodIntent)
        }

        binding.btnRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.email.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please enter an email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.passwd.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please enter a password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.firstName.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Required field firstname.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.lastName.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Required field lastname.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val firstname: String = binding.firstName.text.toString().trim{ it <= ' '}
                    val lastname: String = binding.lastName.text.toString().trim{ it <= ' '}
                    val email: String = binding.email.text.toString().trim{ it <= ' '}
                    val password: String = binding.passwd.text.toString().trim{ it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        // when account has been made, set it
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {
                                    // task gives result, make a firebase user.
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    val docData = hashMapOf(
                                        "firstname" to firstname,
                                        "lastname" to lastname
                                    )

                                    val db = FirebaseFirestore.getInstance()

                                    db.collection("user").document(firebaseUser.uid)
                                        .set(docData)
                                        .addOnSuccessListener { Log.d("persist user", "DocumentSnapshot successfully written!") }
                                        .addOnFailureListener { e -> Log.w("persist user", "Error writing document", e) }

                                    //databaseRefrence.child("user").child(firebaseUser.uid).child("firstname").setValue(firstname)
                                    //databaseRefrence.child("user").child(firebaseUser.uid).child("lastname").setValue(lastname)

                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Your account has been made.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                                    // remove all 'layers' created by switching login/register intent
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }
            }

        }

        setContentView(binding.root)
    }


}