package com.diogo.tasktracker.service;

import com.diogo.tasktracker.model.Task;
import com.diogo.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public Task addTask(String description){
        List<Task> tasks = taskRepository.load();

        int id = nextId(tasks);
        Task task = new Task(id, description);

        tasks.add(task);
        taskRepository.save(tasks);

        return task;
    }

    private int nextId(List<Task> tasks){
        int max = 0;
        for(Task task : tasks){
            if(task.getId() > max){
                max = task.getId();
            }
        }
        return max + 1;
    }
}
