package com.itesm.foodykraveproyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public fun login(v : View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    public fun signup(v : View?) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}