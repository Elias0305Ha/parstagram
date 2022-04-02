package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.ComposeFragment
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // queryPosts()

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemReselectedListener {

            item ->

            var fragmentToShow: Fragment? = null
            when(item.itemId){

                R.id.action_home -> {

                    fragmentToShow = FeedFragment()

                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()

                }
                R.id.action_compose -> {

                    fragmentToShow = ComposeFragment()
                }
                R.id.action_profile -> {

                    fragmentToShow = ProfileFragment()
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }

            }

            if(fragmentToShow != null)
            {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()

            }

            // returns to say that we have handled this user interaction on the item
            true
        }
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
    }

    companion object {
        const val TAG = "MainActivity"
    }
    // query for all posts in our server
}