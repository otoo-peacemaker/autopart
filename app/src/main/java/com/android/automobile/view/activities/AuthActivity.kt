package com.android.automobile.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.automobile.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}