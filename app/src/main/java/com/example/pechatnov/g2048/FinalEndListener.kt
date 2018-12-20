package com.example.pechatnov.g2048

import android.animation.Animator
import android.util.Log

abstract class FinalEndListener(count: Int) : Animator.AnimatorListener {
    var count = count;

    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationStart(animation: Animator?) {}
    override fun onAnimationEnd(animation: Animator?) {
        count -= 1
        if (count == 0) {
            onAnimationFinalEnd()
        }
    }
    abstract fun onAnimationFinalEnd()
}