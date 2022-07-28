package com.tanvir.advanceanimations

import android.animation.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout
import com.tanvir.advanceanimations.databinding.ActivityDetailedBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedBinding
    var bundle = ""
    var parts : List<String> = listOf()
    private lateinit var url : TextView
    private lateinit var status : TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        url = binding.myHeader.url
        status = binding.myHeader.status
        button = binding.myHeader.button

        bundle = intent.getStringExtra("status").toString()
        val delimiter = " "
        val intent = Intent(applicationContext, MainActivity::class.java)
        button.setOnClickListener {
            startActivity(intent)
        }
        parts = bundle.split(delimiter)
        coordinateMotion()
        dotValueAnimation()
        statusObjectAnimation()
    }
    private fun coordinateMotion() {
        val appBarLayout: AppBarLayout = findViewById(R.id.appbar_layout)
        val motionLayout: MotionLayout = findViewById(R.id.motion_layout)

        val listener = AppBarLayout.OnOffsetChangedListener { unused, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)
    }

    private fun statusObjectAnimation(){
        val x = PropertyValuesHolder.ofFloat(
            View.SCALE_X,
            0.5f,
            1f
        )
        val y = PropertyValuesHolder.ofFloat(
            View.SCALE_Y,
            0.5f,
            1f
        )
        val a = PropertyValuesHolder.ofFloat(
            View.ALPHA,
            1f,
            0f
        )
        val b = PropertyValuesHolder.ofFloat(
            View.ALPHA,
            0f,
            1f
        )
        val animatorOne = ObjectAnimator.ofFloat(url,View.ALPHA,0f).apply {
            duration = 1000
        }
        val animatorTwo = ObjectAnimator.ofPropertyValuesHolder(url, x,y,a,b).apply {
            duration =1000
            interpolator = OvershootInterpolator()
        }
        animatorTwo.start()
        animatorTwo.modifyViewDuringAnimation(url,status)
    }

    private fun dotValueAnimation(){
        val spannable = SpannableString("Hang On...")
        url.setText(spannable, TextView.BufferType.SPANNABLE)
        val spannableText = url.text as Spannable
        val trans = ForegroundColorSpan(Color.TRANSPARENT)
        ValueAnimator.ofInt(0, 4).apply {
            repeatCount = 10
            duration = 1000
            addUpdateListener { valueAnimator ->
                val dotsCount = valueAnimator.animatedValue as Int
                if (dotsCount < 4) {
                    spannableText.setSpan(trans,
                        7 + dotsCount,
                        10,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    url.invalidate()
                }
            }
        }.start()
    }

    private fun ObjectAnimator.modifyViewDuringAnimation(view: TextView, view2: TextView){
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                view.text = parts[1]
                view2.text = parts[0]
                if (parts[0] == "Success") view2.setTextColor(Color.GREEN)
                else view2.setTextColor(Color.RED)
            }
        })
    }
}
