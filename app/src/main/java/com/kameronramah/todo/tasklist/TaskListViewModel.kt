package com.kameronramah.todo.tasklist

import androidx.lifecycle.*
import com.kameronramah.todo.network.TasksRepository
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val repository = TasksRepository()
    private val _taskList = MutableLiveData<List<Task>>()
    public val taskList: LiveData<List<Task>> = _taskList

    fun loadTasks() {
        viewModelScope.launch {
            val fetchedTasks = repository.loadTasks()
            if(fetchedTasks != null) {
                _taskList.value = fetchedTasks!!
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if(repository.removeTask(task)) {
                val editableList = _taskList.value.orEmpty().toMutableList()
                editableList.remove(task)
                _taskList.value = editableList
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            if(repository.createTask(task) != null) {
                val editableList = _taskList.value.orEmpty().toMutableList()
                editableList.add(task)
                _taskList.value = editableList
            }
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = repository.updateTask(task)
            if(updatedTask != null) {
                val editableList = _taskList.value.orEmpty().toMutableList()
                val position = editableList.indexOfFirst { updatedTask?.id == it.id }
                editableList[position] = updatedTask
                _taskList.value = editableList
            }
        }
    }

}