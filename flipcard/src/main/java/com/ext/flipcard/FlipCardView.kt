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

    private val cardViews = ArrayList<View>()
    private var currentIndex = 0

    // XML attributes
    private var flipDirectionHorizontal = true
    private var flipDuration = 400L
    private var enableShadow = true

    // Side attributes
    private var flipFromLeft = true
    private var flipFromTop = true

    init {
        cameraDistance = 8000 * resources.displayMetrics.density

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


    /** ADD UNLIMITED CARDS */
    fun addCardView(layoutId: Int) {
        val v = LayoutInflater.from(context).inflate(layoutId, this, false)

        // Initial states for all except first card
        if (cardViews.isEmpty()) {
            v.alpha = 1f
            v.rotationY = 0f
            v.rotationX = 0f
        } else {
            if (flipDirectionHorizontal) {
                v.rotationY = if (flipFromLeft) -90f else 90f
            } else {
                v.rotationX = if (flipFromTop) -90f else 90f
            }
            v.alpha = 0f
        }

        cardViews.add(v)
        addView(v)
    }


    /** FLIP TO NEXT CARD */
    fun nextCard() {
        if (cardViews.size < 2) return

        val current = cardViews[currentIndex]
        val nextIndex = (currentIndex + 1) % cardViews.size
        val next = cardViews[nextIndex]

        flipBetween(current, next)
        currentIndex = nextIndex
    }


    /** FLIP TO PREVIOUS CARD */
    fun prevCard() {
        if (cardViews.size < 2) return

        val current = cardViews[currentIndex]
        val prevIndex = if (currentIndex - 1 < 0) cardViews.size - 1 else currentIndex - 1
        val prev = cardViews[prevIndex]

        flipBetween(current, prev)
        currentIndex = prevIndex
    }


    /** CORE FLIP ANIMATION BETWEEN TWO CARD VIEWS */
    private fun flipBetween(fromView: View, toView: View) {

        val outAnim = when {
            flipDirectionHorizontal && flipFromLeft  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
            flipDirectionHorizontal && !flipFromLeft -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out_alt)
            !flipDirectionHorizontal && flipFromTop  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_out)
            else -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_out_alt)
        }

        val inAnim = when {
            flipDirectionHorizontal && flipFromLeft  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)
            flipDirectionHorizontal && !flipFromLeft -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in_alt)
            !flipDirectionHorizontal && flipFromTop  -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_in)
            else -> AnimatorInflater.loadAnimator(context, R.animator.card_flip_top_in_alt)
        }

        outAnim.duration = flipDuration / 2
        inAnim.duration = flipDuration / 2

        // LIFT SHADOW
        if (enableShadow) {
            animate().translationZ(20f).setDuration(flipDuration / 3).start()
        }

        outAnim.setTarget(fromView)
        inAnim.setTarget(toView)

        fromView.bringToFront()

        outAnim.start()
        outAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                inAnim.start()
            }
        })

        // DROP SHADOW
        if (enableShadow) {
            postDelayed({
                animate().translationZ(0f).setDuration(flipDuration / 3).start()
            }, flipDuration)
        }
    }
    fun addCard(view: View) {
        prepareCardInitialState(view)
        cardViews.add(view)
        addView(view)
    }
    private fun prepareCardInitialState(v: View) {
        if (cardViews.isEmpty()) {
            // first card visible
            v.alpha = 1f
            v.rotationY = 0f
            v.rotationX = 0f
        } else {
            // hidden based on flip direction
            if (flipDirectionHorizontal) {
                v.rotationY = if (flipFromLeft) -90f else 90f
            } else {
                v.rotationX = if (flipFromTop) -90f else 90f
            }
            v.alpha = 0f
        }
    }

}
