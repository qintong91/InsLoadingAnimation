package com.example.qintong.animatordemo

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.qintong.library.InsLoadingView
import kotlinx.android.synthetic.main.activity_main.*

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loading_view?.setCircleDuration(2000)
        loading_view?.setRotateDuration(10000)
        loading_view?.setStartColor(Color.YELLOW)
        loading_view?.setEndColor(Color.BLUE)
        loading_view?.setOnClickListener {
            when (loading_view?.status) {
                InsLoadingView.Status.UNCLICKED -> loading_view?.status = InsLoadingView.Status.LOADING
                InsLoadingView.Status.LOADING -> loading_view?.status = InsLoadingView.Status.CLICKED
                InsLoadingView.Status.CLICKED -> loading_view?.status = InsLoadingView.Status.UNCLICKED
            }
            Toast.makeText(this@DemoActivity, "click !", LENGTH_SHORT).show()
        }
        loading_view?.setOnLongClickListener {
            Toast.makeText(this@DemoActivity, "long click !", LENGTH_SHORT).show()
            true
        }
    }
}
