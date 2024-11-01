package com.example.chatfirebase

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val anim = findViewById<LottieAnimationView>(R.id.animLoad)
        anim.speed = 0.75F
        anim.playAnimation()
        anim.repeatCount = 0

        anim.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationEnd(p0: Animator) {
                startActivity(Intent(this@SplashActivity, ScrollActivity::class.java))
                finish()
            }

            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}
        })
    }
}
