package com.kameronramah.todo.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kameronramah.todo.R
import com.kameronramah.todo.network.TasksDiffCallBack


class TaskListAdapter() : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallBack) {

    var onDeleteTask: ((Task) -> Unit)? = null
    var onEditTask: ((Task) -> Unit)? = null

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            itemView.apply { // `apply {}` permet d'éviter de répéter `itemView.*`
                val itemViewReferenceTitle: TextView = findViewById(R.id.task_title)
                itemViewReferenceTitle.text = task.title
                val itemViewReferenceDesc: TextView = findViewById(R.id.task_desc)
                itemViewReferenceDesc.text = task.description

                val imageButtonEdit = findViewById<ImageButton>(R.id.imageButtonEdit)
                imageButtonEdit.setOnClickListener {
                    onEditTask?.invoke(task)
                }

                val imageButtonDelete = findViewById<ImageButton>(R.id.imageButtonDelete)
                imageButtonDelete.setOnClickListener {
                    onDeleteTask?.invoke(task)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}