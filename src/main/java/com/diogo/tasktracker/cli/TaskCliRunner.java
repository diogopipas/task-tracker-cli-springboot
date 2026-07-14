package com.diogo.tasktracker.cli;

import com.diogo.tasktracker.model.Task;
import com.diogo.tasktracker.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskCliRunner implements CommandLineRunner{

    private final TaskService taskService;

    public TaskCliRunner(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0){
            System.out.println("No command provided. Try: add \"your task\"");
            return;
        }
        String command = args[0];

        switch (command) {
            case "add" -> {
                if (args.length < 2) {
                    System.out.println("Usage: add \"your task description\"");
                    return;
                }
                String description = args[1];
                Task task = taskService.addTask(description);
                System.out.println("Task added successfully (ID: " + task.getId() + ")");
            }
            default -> System.out.println("Unknown command: " + command);
        }
    }
}
