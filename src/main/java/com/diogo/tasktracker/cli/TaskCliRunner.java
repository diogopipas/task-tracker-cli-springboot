package com.diogo.tasktracker.cli;

import com.diogo.tasktracker.model.Task;
import com.diogo.tasktracker.service.TaskService;

import java.util.List;

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
        try{
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
                case "update" -> {
                    if (args.length < 3) {
                        System.out.println("Usage: update <id> \"your task description\"");
                        return;
                    }
                    int id = Integer.parseInt(args[1]);
                    String description = args[2];
                    taskService.updateTask(id, description);
                    System.out.println("Task " + id + " updated successfully");
                }
                case "list" -> {
                    String status = (args.length == 2) ? args[1] : null;
                    List<Task> tasks = taskService.listTasks(status);
                    if(tasks.isEmpty()){
                        System.out.println("No tasks found.");
                    } else {
                        for(Task task : tasks) {
                            System.out.println(task);
                        }
                    }
                }
                case "delete" -> {
                    if(args.length < 2){
                        System.out.println("Usage: delete <id>");
                        return;
                    }
                    int id = Integer.parseInt(args[1]);
                    taskService.deleteTask(id);
                    System.out.println("Task " + id + " was successfully deleted");
                }
                case "mark" -> {
                    if(args.length < 3){
                        System.out.println("Usage: mark <todo/in-progress/done> <id>");
                        return;
                    }
                    String status = args[1];
                    int id = Integer.parseInt(args[2]);

                    taskService.markTask(status, id);
                    
                    System.out.println("Task " + id + "'s status was successfully changed to " + status);
                }
                default -> System.out.println("Unknown command: " + command);
            }
        } catch (NumberFormatException e){
            System.out.println("The id must be a whole number.");
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
