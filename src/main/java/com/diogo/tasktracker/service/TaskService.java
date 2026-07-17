package com.diogo.tasktracker.service;

import com.diogo.tasktracker.model.Task;
import com.diogo.tasktracker.model.Status;
import com.diogo.tasktracker.repository.TaskRepository;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Task findById(List<Task> tasks, int id){
        for(Task task : tasks){
            if(task.getId() == id){
                return task;
            }
        }
        return null;    
    }

    public Task updateTask(int id, String description){
        List<Task> tasks = taskRepository.load();

        Task task = findById(tasks, id);
        if (task == null){
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

        task.setDescription(description);
        task.setUpdatedAt(LocalDateTime.now());


        taskRepository.save(tasks);
        return task;
    }

    public List<Task> listTasks(String status){
        List<Task> tasks = taskRepository.load();
        ArrayList<Task> filteredTasks = new ArrayList<>();

        if(status == null){
            return tasks;
        }
        else{
            for(Task task : tasks){
                if(task.getStatus().getValue().equals(status)){
                    filteredTasks.add(task);
                }
            }
        }
        return List.copyOf(filteredTasks);
    }

    public Task deleteTask(int id){
        List<Task> tasks = taskRepository.load();

        Task task = findById(tasks, id);

        if(task == null){
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

        tasks.remove(tasks.indexOf(task));
        taskRepository.save(tasks);
        return task;
    }

    public Task markTask(String status, int id){
        List<Task> tasks = taskRepository.load();

        Task task = findById(tasks, id);

        if(task == null){
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

        task.setStatus(Status.fromValue(status));
        task.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(tasks);
        return task;
        
    }


}
