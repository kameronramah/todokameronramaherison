package com.kameronramah.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kameronramah.todo.R
import com.kameronramah.todo.network.Api
import com.kameronramah.todo.network.TasksRepository
import com.kameronramah.todo.task.TaskActivity
import com.kameronramah.todo.task.TaskActivity.Companion.ADD_TASK_REQUEST_CODE
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    // On récupère une instance de ViewModel
    private val viewModel: TaskListViewModel by viewModels()

   private val adapter = TaskListAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_task_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter




        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(activity, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }


        adapter.onDeleteTask = { task ->
            viewModel.deleteTask(task)
        }

        adapter.onEditTask = { task ->
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_KEY, task)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        viewModel.taskList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.toList())
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task = data?.getSerializableExtra(TaskActivity.TASK_KEY) as? Task

        if (requestCode == TaskActivity.ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK && task != null) {
            val index = viewModel.taskList.value?.indexOfFirst { it.id == task.id }
            if(index == -1) {
                viewModel.addTask(task)
            }
            else {
                viewModel.editTask(task)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTasks()
        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            val userInfoTextView = view?.findViewById<TextView>(R.id.infoUserTextView)
            userInfoTextView?.text = "${userInfo.firstName} ${userInfo.lastName}"
        }

    }

}

