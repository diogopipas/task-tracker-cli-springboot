package com.diogo.tasktracker.service;

import com.diogo.tasktracker.model.Status;
import com.diogo.tasktracker.model.Task;
import com.diogo.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void addTask_assignsId1AndTodoStatus_whenNoTasksExist(){
        // Arrange: The repository has no tasks yet
        when(taskRepository.load()).thenReturn(new ArrayList<>());

        // Act: add a task
        Task result = taskService.addTask("Buy groceries");

        // Assert: it got id 1, our description, and started as TODO
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getDescription()).isEqualTo("Buy groceries");
        assertThat(result.getStatus()).isEqualTo(Status.TODO);

        // Assert (interaction): the service saved exactly once
        verify(taskRepository, times(1)).save(anyList());
    }

    @Test
    void updateTask_changesDescriptionButKeepsStatus_whenTaskExists(){
        //Arrange
        Task existing = new Task(1, "Old description");
        ArrayList<Task> newList = new ArrayList<>(); 
        newList.add(existing);
        when(taskRepository.load()).thenReturn(newList);

        //Act
        Task result = taskService.updateTask(1, "New description");

        //Assert
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(Status.TODO);
        assertThat(result.getDescription()).isEqualTo("New description");

        //Assert
        verify(taskRepository, times(1)).save(anyList());

    }

    @Test
    void updateTask_throwsException_whenTaskDoesNotExist(){
        // Arrange
        when(taskRepository.load()).thenReturn(new ArrayList<>());

        // Act + Assert

        assertThatThrownBy(() -> taskService.updateTask(99, "whatever"))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("Task not found with id: 99");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void listTasks_returnsOnlyMatchingTasks_whenStatusGiven(){
        //Arrange
        ArrayList<Task> taskList = new ArrayList<>();
        Task task1 = new Task(1, "First task");
        Task task2 = new Task(2, "Second task");
        Task task3 = new Task(3, "Third task");
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);

        task2.setStatus(Status.IN_PROGRESS);
        task3.setStatus(Status.DONE);

        when(taskRepository.load()).thenReturn(taskList);

        //Act
        List<Task> result = taskService.listTasks("done");

        //Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.DONE);

    }
}
