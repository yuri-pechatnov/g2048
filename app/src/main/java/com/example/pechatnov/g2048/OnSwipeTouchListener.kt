package com.example.pechatnov.g2048

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {
    val gestureDetector = GestureDetector(ctx, GestureListener(this))

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private final class GestureListener(listener: OnSwipeTouchListener) : GestureDetector.SimpleOnGestureListener() {
        private val listener = listener
        private val SWIPE_THRESHOLD: Int = 100
        private val SWIPE_VELOCITY_THRESHOLD: Int = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true;
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            listener.onSwipeRight();
                        } else {
                            listener.onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        listener.onSwipeBottom();
                    } else {
                        listener.onSwipeTop();
                    }
                    result = true;
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public open fun onSwipe(dir: Int) {}

    public open fun onSwipeTop() {
        onSwipe(3)
    }
    public open fun onSwipeRight() {
        onSwipe(2)
    }
    public open fun onSwipeLeft() {
        onSwipe(0)
    }
    public open fun onSwipeBottom() {
        onSwipe(1)
    }
}