package com.example.musicaptitude

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.io.IOException

class TestActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var sameDifferentGroup: RadioGroup
    private lateinit var noteChoiceGroup: RadioGroup
    private lateinit var whichNoteText: TextView
    private lateinit var playButton: MaterialButton
    private lateinit var submitButton: MaterialButton
    private lateinit var progressText: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var currentQuestionIndex = 0
    private val results = mutableListOf<AnswerResult>()

    private lateinit var questions: List<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // UI elements
        questionText = findViewById(R.id.questionText)
        sameDifferentGroup = findViewById(R.id.sameDifferentGroup)
        noteChoiceGroup = findViewById(R.id.noteChoiceGroup)
        whichNoteText = findViewById(R.id.whichNoteText)
        playButton = findViewById(R.id.playButton)
        submitButton = findViewById(R.id.submitButton)
        progressText = findViewById(R.id.progressText)

        // Load questions from JSON
        questions = loadQuestionsFromAssets()

        // Set up listeners
        playButton.setOnClickListener { playAudio() }
        submitButton.setOnClickListener { onSubmit() }

        sameDifferentGroup.setOnCheckedChangeListener { _, checkedId ->
            val selected = findViewById<RadioButton>(checkedId)
            val answer = selected?.text.toString()
            if (getCurrentQuestion().type == "MELODY" && answer == "Different") {
                noteChoiceGroup.visibility = View.VISIBLE
                whichNoteText.visibility = View.VISIBLE
            } else {
                noteChoiceGroup.clearCheck()
                noteChoiceGroup.visibility = View.GONE
                whichNoteText.visibility = View.GONE
            }
        }

        showQuestion()
    }

    private fun loadQuestionsFromAssets(): List<Question> {
        return try {
            val jsonString = assets.open("questions.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listType = object : TypeToken<List<Question>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getCurrentQuestion(): Question = questions[currentQuestionIndex]

    private fun playAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            val afd = assets.openFd("audio/" + getCurrentQuestion().audioFile)
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Audio file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showQuestion() {
        val question = getCurrentQuestion()
        progressText.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
        questionText.text = question.questionText

        sameDifferentGroup.removeAllViews()
        question.options.forEach { optionText ->
            val radioButton = RadioButton(this)
            radioButton.text = optionText
            sameDifferentGroup.addView(radioButton)
        }

        noteChoiceGroup.removeAllViews()
        question.followUpOptions?.forEach { note ->
            val radioButton = RadioButton(this)
            radioButton.text = note
            noteChoiceGroup.addView(radioButton)
        }

        noteChoiceGroup.visibility = View.GONE
        whichNoteText.visibility = View.GONE
        sameDifferentGroup.clearCheck()
        noteChoiceGroup.clearCheck()
    }

    private fun onSubmit() {
        val selectedOptionId = sameDifferentGroup.checkedRadioButtonId
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedOption = findViewById<RadioButton>(selectedOptionId).text.toString()
        val question = getCurrentQuestion()

        var fullAnswer = selectedOption
        if (question.type == "MELODY" && selectedOption == "Different") {
            val selectedNoteId = noteChoiceGroup.checkedRadioButtonId
            if (selectedNoteId == -1) {
                Toast.makeText(this, "Please select which note changed.", Toast.LENGTH_SHORT).show()
                return
            }
            val selectedNote = findViewById<RadioButton>(selectedNoteId).text.toString()
            fullAnswer = "$selectedOption (Note $selectedNote)"
        }

        val correct = if (question.type == "MELODY") {
            "${question.correctAnswer} (Note ${question.correctFollowUp})"
        } else {
            question.correctAnswer
        }

        results.add(
            AnswerResult(
                question = question.questionText,
                userAnswer = fullAnswer,
                correctAnswer = correct
            )
        )

        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            showQuestion()
        } else {
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putParcelableArrayListExtra("results", ArrayList(results))
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
