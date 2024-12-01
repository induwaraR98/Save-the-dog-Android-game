package com.example.savethedog


import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity(), GameTask {
    private lateinit var rootLayout: LinearLayout
    private lateinit var startBtn: Button
    private lateinit var logo: ImageView
    private lateinit var mGameView: GameView
    private lateinit var score: TextView
    private lateinit var highScoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.startBtn)
        logo = findViewById(R.id.logo)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        highScoreTextView = findViewById(R.id.highScoreTextView)
        mGameView = GameView(this, this)

        // Set initial high score text
        highScoreTextView.text = "High Score: ${getHighScore()}"

        startBtn.setOnClickListener {
            startGame()
        }

        score.setOnClickListener {
            toggleHighScoreVisibility()
        }
    }


    private fun startGame() {
        mGameView.setBackgroundResource(R.drawable.background)
        rootLayout.addView(mGameView)
        logo.visibility = ImageView.GONE
        startBtn.visibility = View.GONE
        score.visibility = View.GONE
        highScoreTextView.visibility = View.GONE
    }

    private fun toggleHighScoreVisibility() {
        highScoreTextView.visibility = if (highScoreTextView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        highScoreTextView.text = "High Score: ${getHighScore()}"
    }

    override fun closeGame(mScore: Int) {
        score.text = "Score: $mScore"

        val currentHighScore = getHighScore()
        if (mScore > currentHighScore) {
            saveHighScore(mScore)
        }
        highScoreTextView.text = "High Score: ${getHighScore()}"

        rootLayout.removeView(mGameView)
        logo.visibility = ImageView.VISIBLE
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        highScoreTextView.visibility = View.VISIBLE

        mGameView.resetGameState()
    }

    private fun saveHighScore(score: Int) {
        val sharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(HIGH_SCORE_KEY, score)
        editor.apply()
    }

    private fun getHighScore(): Int {
        val sharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
    }

    companion object {
        private const val HIGH_SCORE_KEY = "high_score"
    }
}
