package com.ext.flipcard

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
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

    // XML attributes
    private var flipDirectionHorizontal = true
    private var flipDuration = 400L
    private var enableShadow = true

    // Direction side attributes
    private var flipFromLeft = true   // true = left→right, false = right→left
    private var flipFromTop = true    // true = top→bottom, false = bottom→top


    init {
        cameraDistance = 8000 * resources.displayMetrics.density

        // Read XML attributes
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.FlipCardView)

            flipDirectionHorizontal =
                a.getInt(R.styleable.FlipCardView_flipDirection, 0) == 0

            flipDuration =
                a.getInt(R.styleable.FlipCardView_flipDuration, 400).toLong()

            enableShadow =
                a.getBoolean(R.styleable.FlipCardView_flipShadow, true)

            flipFromLeft =
                a.getInt(R.styleable.FlipCardView_flipHorizontalSide, 0) == 0

            flipFromTop =
                a.getInt(R.styleable.FlipCardView_flipVerticalSide, 0) == 0

            a.recycle()
        }
    }


    fun setFrontView(layoutId: Int) {
        frontView = LayoutInflater.from(context).inflate(layoutId, this, false)

        frontView.rotationY = 0f
        frontView.rotationX = 0f
        frontView.alpha = 1f
        frontView.visibility = View.VISIBLE

        addView(frontView)
    }


    fun setBackView(layoutId: Int) {
        backView = LayoutInflater.from(context).inflate(layoutId, this, false)

        // Correct hidden state depending on flip direction + side
        if (flipDirectionHorizontal) {
            // Horizontal flip → hide on Y axis
            backView.rotationY = if (flipFromLeft) -90f else 90f
        } else {
            // Vertical flip → hide on X axis
            backView.rotationX = if (flipFromTop) -90f else 90f
        }

        backView.alpha = 0f
        backView.visibility = View.VISIBLE

        addView(backView)
    }


    fun flip(
        duration: Long = flipDuration,
        horizontal: Boolean = flipDirectionHorizontal
    ) {

        val outAnim = when {
            horizontal && flipFromLeft  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
            horizontal && !flipFromLeft -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out_alt)
            !horizontal && flipFromTop  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_out)
            else -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_out_alt)
        }

        val inAnim = when {
            horizontal && flipFromLeft  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)
            horizontal && !flipFromLeft -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in_alt)
            !horizontal && flipFromTop  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_in)
            else -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_in_alt)
        }


        outAnim.duration = duration / 2
        inAnim.duration = duration / 2

        // LIFT SHADOW
        if (enableShadow) {
            animate().translationZ(20f).setDuration(duration / 3).start()
        }


        if (isBackVisible) {

            // Show FRONT side
            outAnim.setTarget(backView)
            inAnim.setTarget(frontView)

            outAnim.start()
            outAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    inAnim.start()
                }
            })

        } else {

            // Show BACK side
            outAnim.setTarget(frontView)
            inAnim.setTarget(backView)

            outAnim.start()
            outAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    inAnim.start()
                }
            })
        }

        // DROP SHADOW
        if (enableShadow) {
            postDelayed({
                animate().translationZ(0f).setDuration(duration / 3).start()
            }, duration)
        }

        isBackVisible = !isBackVisible
    }

}
