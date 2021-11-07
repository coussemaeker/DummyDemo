package com.ddrcss.android.dummydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ddrcss.android.dummydemo.ui.UserListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListFragment()
    }

    private fun initListFragment(): Unit {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, UserListFragment(), "USER_LIST").commit()
    }

}