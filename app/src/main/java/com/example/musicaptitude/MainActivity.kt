package com.example.musicaptitude

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.parcelize.Parcelize

class MainActivity : AppCompatActivity() {

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

    private val questions = listOf(
        Question(
            type = QuestionType.MELODY,
            audioResId = R.raw.m1m02p,
            questionText = "Is the second melody the same as the first?",
            options = listOf("Same", "Different"),
            followUpOptions = listOf("1", "2", "3", "4"),
            correctAnswer = "Different",
            correctFollowUp = "3"
        ),
        Question(
            type = QuestionType.TEXTURE,
            audioResId = R.raw.m1t01p,
            questionText = "Listen to the audio and decide if the chord has 2, 3, or 4 notes.",
            options = listOf("2", "3", "4"),
            followUpOptions = null,
            correctAnswer = "2"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI elements
        questionText = findViewById(R.id.questionText)
        sameDifferentGroup = findViewById(R.id.sameDifferentGroup)
        noteChoiceGroup = findViewById(R.id.noteChoiceGroup)
        whichNoteText = findViewById(R.id.whichNoteText)
        playButton = findViewById(R.id.playButton)
        submitButton = findViewById(R.id.submitButton)
        progressText = findViewById(R.id.progressText)

        // Setup listeners
        playButton.setOnClickListener { playAudio() }
        submitButton.setOnClickListener { onSubmit() }

        // Radio group change listener
        sameDifferentGroup.setOnCheckedChangeListener { _, checkedId ->
            val selected = findViewById<RadioButton>(checkedId)
            val answer = selected?.text.toString()
            if (getCurrentQuestion().type == QuestionType.MELODY && answer == "Different") {
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

    private fun getCurrentQuestion(): Question = questions[currentQuestionIndex]

    private fun playAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, getCurrentQuestion().audioResId)
        mediaPlayer?.start()
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
        if (question.type == QuestionType.MELODY && selectedOption == "Different") {
            val selectedNoteId = noteChoiceGroup.checkedRadioButtonId
            if (selectedNoteId == -1) {
                Toast.makeText(this, "Please select which note changed.", Toast.LENGTH_SHORT).show()
                return
            }
            val selectedNote = findViewById<RadioButton>(selectedNoteId).text.toString()
            fullAnswer = "$selectedOption (Note $selectedNote)"
        }

        val correct = when (question.type) {
            QuestionType.MELODY -> "${question.correctAnswer} (Note ${question.correctFollowUp})"
            QuestionType.TEXTURE -> question.correctAnswer
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

enum class QuestionType {
    MELODY,
    TEXTURE
}

data class Question(
    val type: QuestionType,
    val audioResId: Int,
    val questionText: String,
    val options: List<String>,
    val followUpOptions: List<String>? = null,
    val correctAnswer: String,
    val correctFollowUp: String? = null
)



