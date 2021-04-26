package com.kameronramah.todo.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.kameronramah.todo.R
import com.kameronramah.todo.tasklist.Task
import java.util.*

class TaskActivity : AppCompatActivity() {

    companion object {
        const val TASK_KEY = "task"
        const val ADD_TASK_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val validButton = findViewById<Button>(R.id.validButton)
        val descActivity = findViewById<EditText>(R.id.editTextDescription)
        val titleActivity = findViewById<EditText>(R.id.editTextTitle)

        val task = intent.getSerializableExtra(TASK_KEY) as? Task
        titleActivity.setText(task?.title)
        descActivity.setText(task?.description)

        validButton.setOnClickListener {
            val newTask = Task(
                    id = task?.id ?: UUID.randomUUID().toString(),
                    title = titleActivity.text.toString(),
                    description = descActivity.text.toString()
            )
            intent.putExtra(TASK_KEY, newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}