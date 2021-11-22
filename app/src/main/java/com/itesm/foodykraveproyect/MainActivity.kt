package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(v : View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun signup(v : View?) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}