package com.google.firebase.quickstart.database.kotlin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.quickstart.database.R
import com.google.firebase.quickstart.database.kotlin.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity(), View.OnClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Click listeners
        buttonSignIn.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()

        // Check auth on Activity start
        //        auth.currentUser?.let {
        //            onSignUpSuccess(it)
        //        }

    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        if (!validateForm()) {
            return
        }

        showProgressDialog()
        val email = fieldEmail.text.toString()
        val password = fieldPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)
            hideProgressDialog()

            if (task.isSuccessful) {
                goToMainActivity()
            } else {
                Toast.makeText(baseContext, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signUp() {
        Log.d(TAG, "signUp")
        if (!validateForm()) {
            return
        }

        showProgressDialog()
        val email = fieldEmail.text.toString()
        val password = fieldPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            Log.d(TAG, "createUser:onComplete:" + task.isSuccessful)
            hideProgressDialog()

            if (task.isSuccessful) {
                onSignUpSuccess(task.result?.user!!)
            } else {
                Toast.makeText(baseContext, "Sign Up Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSignUpSuccess(user: FirebaseUser) {
        val username = usernameFromEmail(user.email!!)

        // Write new user
        writeNewUser(user.uid, username, user.email)

        // Go to MainActivity
        goToMainActivity()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    private fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(fieldEmail.text.toString())) {
            fieldEmail.error = "Required"
            result = false
        } else {
            fieldEmail.error = null
        }

        if (TextUtils.isEmpty(fieldPassword.text.toString())) {
            fieldPassword.error = "Required"
            result = false
        } else {
            fieldPassword.error = null
        }

        return result
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email,"demo@demo")
        database.child("users").child(userId).setValue(user)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.buttonSignIn) {
            signIn()
        } else if (i == R.id.buttonSignUp) {
            signUp()
        }
    }


}
