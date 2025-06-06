package com.example.musicaptitude

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        @Suppress("DEPRECATION")
        val resultList = intent.getParcelableArrayListExtra<AnswerResult>("results") ?: arrayListOf()
        val score = resultList.count { it.userAnswer == it.correctAnswer }

        val scoreText = findViewById<TextView>(R.id.scoreText)
        scoreText.text = getString(R.string.score_text, score, resultList.size)

        val recycler = findViewById<RecyclerView>(R.id.resultRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SummaryAdapter(resultList)
    }
}




