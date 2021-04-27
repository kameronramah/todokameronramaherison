package com.kameronramah.todo.network

import androidx.recyclerview.widget.DiffUtil
import com.kameronramah.todo.tasklist.Task

object TasksDiffCallBack : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldTask: Task, newTask: Task) = oldTask.id == newTask.id
    override fun areContentsTheSame(oldTask: Task, newTask: Task) = oldTask == newTask
}
