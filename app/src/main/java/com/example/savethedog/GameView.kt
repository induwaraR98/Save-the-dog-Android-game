package com.example.savethedog


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    private var myPaint: Paint = Paint()
    private var speed = 1
    private var time = 0
    private var score = 0
    private var otherBombs = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0
    var dogPosition = 0

    private val preferences: SharedPreferences = c.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)

    init {
        myPaint = Paint()
    }

    fun resetGameState() {
        otherBombs.clear()
        score = 0
        speed = 1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = measuredWidth
        viewHeight = measuredHeight

        // Generate other ships randomly
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherBombs.add(map)
        }

        // Update game time
        time += 10 + speed

        // Set up drawing properties
        myPaint.style = Paint.Style.FILL

        // Draw the player's ship
        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10

        val d = resources.getDrawable(R.drawable.dog, null)
        d.setBounds(
            dogPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            dogPosition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2
        )
        d.draw(canvas)
        myPaint.color = Color.GREEN
        var highScore = getHighScore()

        for (i in otherBombs.indices) {
            try {
                val shipX = otherBombs[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val shipY = time - otherBombs[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.bomb, null)

                d2.setBounds(
                    shipX + 25, shipY - carHeight, shipX + carWidth - 25, shipY
                )
                d2.draw(canvas)
                if (otherBombs[i]["lane"] as Int == dogPosition) {
                    if (shipY > viewHeight - 2 - carHeight && shipY < viewHeight - 2) {
                        gameTask.closeGame(score)
                    }
                }
                if (shipY > viewHeight + carHeight) {
                    otherBombs.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                        saveHighScore(highScore)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        myPaint.color = Color.WHITE
        myPaint.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f, myPaint)
        canvas.drawText("High Score : $highScore", 80f, 140f, myPaint)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (dogPosition > 0) {
                        dogPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (dogPosition < 2) {
                        dogPosition++
                    }
                }
                invalidate() // Redraw the view after updating ship position
            }
            MotionEvent.ACTION_UP -> {

            }

        }
        return true
    }

    private fun saveHighScore(score: Int) {
        preferences.edit().putInt("HighScore", score).apply()
    }

    private fun getHighScore(): Int {
        return preferences.getInt("HighScore", 0)
    }
}
