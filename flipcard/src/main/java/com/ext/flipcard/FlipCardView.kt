package com.ext.flipcard

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

class FlipCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private lateinit var frontView: View
    private lateinit var backView: View

    private var isBackVisible = false

    init {
        cameraDistance = 8000 * resources.displayMetrics.density
    }

    fun setFrontView(layoutId: Int) {
        frontView = LayoutInflater.from(context).inflate(layoutId, this, false)
        addView(frontView)
    }

    fun setBackView(layoutId: Int) {
        backView = LayoutInflater.from(context).inflate(layoutId, this, false)
        backView.visibility = View.GONE
        addView(backView)
    }

    fun flip(duration: Long = 400, horizontal: Boolean = true) {
        if (!::frontView.isInitialized || !::backView.isInitialized) return

        val animatorOut = if (horizontal)
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
        else
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_out)

        val animatorIn = if (horizontal)
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)
        else
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_in)

        animatorOut.duration = duration
        animatorIn.duration = duration

        animatorOut as AnimatorSet
        animatorIn as AnimatorSet

        if (isBackVisible) {
            animatorOut.setTarget(backView)
            animatorIn.setTarget(frontView)
            backView.visibility = View.GONE
            frontView.visibility = View.VISIBLE
        } else {
            animatorOut.setTarget(frontView)
            animatorIn.setTarget(backView)
            frontView.visibility = View.GONE
            backView.visibility = View.VISIBLE
        }

        animatorOut.start()
        animatorIn.start()
        isBackVisible = !isBackVisible
    }
}
