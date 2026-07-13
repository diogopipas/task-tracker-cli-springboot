package com.diogo.tasktracker.repository;

import com.diogo.tasktracker.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.RuntimeErrorException;

@Repository
public class TaskRepository {

    private static final String FILE_NAME = "tasks.json";

    private final ObjectMapper objectMapper;

    // Constructor injection: Spring passes in the ObjectMapper it configured.
    public TaskRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Read every task from the file. If the file doesn't exist yet, start empty.
    public List<Task> load() {
        File file = new File(FILE_NAME);
        if(!file.exists()){
            return new ArrayList<>();
        }
        try {
            Task[] tasks = objectMapper.readValue(file, Task[].class);
            return new ArrayList<>(Arrays.asList(tasks));
        } catch (IOException e) {
            throw new RuntimeException("Could not read tasks from " + FILE_NAME, e);
        }
    }

    public void save(List<Task> tasks){
        try{
            objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(FILE_NAME), tasks);
        } catch (IOException e){
            throw new RuntimeException("Could not write tasks to" + FILE_NAME, e);
        }
    }
}
