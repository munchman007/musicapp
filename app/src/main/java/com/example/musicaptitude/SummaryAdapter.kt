package com.example.musicaptitude

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SummaryAdapter(private val results: List<AnswerResult>) :
    RecyclerView.Adapter<SummaryAdapter.ResultViewHolder>() {

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.questionType)
        val yourAnswer: TextView = view.findViewById(R.id.yourAnswer)
        val correctAnswer: TextView = view.findViewById(R.id.correctAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]
        val context = holder.itemView.context

        holder.questionText.text = context.getString(R.string.question_label, result.question)
        holder.yourAnswer.text = context.getString(R.string.your_answer_label, result.userAnswer)
        holder.correctAnswer.text = context.getString(R.string.correct_answer_label, result.correctAnswer)
    }

    override fun getItemCount(): Int = results.size
}


