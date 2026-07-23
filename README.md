# Task Tracker CLI

A command-line application for tracking what you need to do, what you are working on, and what you have finished — built with Java and Spring Boot.

Tasks are stored as JSON in the directory you run the command from, so each folder can keep its own independent task list.

## Features

- Add, update, and delete tasks
- Mark tasks as `todo`, `in-progress`, or `done`
- List every task, or filter by status
- Automatic JSON persistence — the storage file is created on first use
- Graceful handling of invalid ids, unknown statuses, and missing arguments

## Built with

| Tool | Purpose |
| --- | --- |
| Java 21 | Language (compilation target) |
| Spring Boot 4.1 | Dependency injection and application bootstrap |
| Jackson 2.21 | JSON serialization (with `jsr310` for `LocalDateTime` support) |
| Maven | Build tool — wrapper included, no local install required |
| JUnit 5, Mockito, AssertJ | Testing |

## Prerequisites

- A JDK, version 21 or newer

Maven does **not** need to be installed separately — the project bundles the Maven Wrapper (`./mvnw`).

## Build and setup

Build the executable jar:

```bash
./mvnw package
```

Make the launcher script executable (only needed once):

```bash
chmod +x task-cli
```

You can now run the application from the project folder:

```bash
./task-cli list
```

### Optional: run `task-cli` from any directory

Add the project directory to your `PATH` by putting this line in `~/.zshrc` (or `~/.bashrc`), then opening a new terminal:

```bash
export PATH="$PATH:/path/to/task-tracker-cli-springboot"
```

You can then drop the `./` and simply run `task-cli`. The task file is always created in whichever directory you run the command from.

## Usage

> **Note:** wrap multi-word descriptions in quotes, otherwise your shell splits them into separate arguments and only the first word is captured.

### Add a task

```bash
task-cli add "Buy groceries"
# Task added successfully (ID: 1)
```

### Update a task's description

```bash
task-cli update 1 "Buy groceries and cook dinner"
# Task 1 updated successfully
```

Updating changes the description and refreshes `updatedAt`. The task's id, status, and original `createdAt` are preserved.

### Change a task's status

```bash
task-cli mark in-progress 1
# Task 1's status was successfully changed to in-progress

task-cli mark done 1
# Task 1's status was successfully changed to done
```

Valid statuses are `todo`, `in-progress`, and `done`.

### Delete a task

```bash
task-cli delete 1
# Task 1 was successfully deleted
```

### List tasks

List everything:

```bash
task-cli list
# Task 1: Buy groceries and cook dinner, Status: todo
# Task 2: Write report, Status: in-progress
```

Or filter by status:

```bash
task-cli list todo
task-cli list in-progress
task-cli list done
```

When nothing matches, the output is:

```bash
task-cli list done
# No tasks found.
```

### Error handling

Invalid input produces a readable message instead of a stack trace:

```bash
task-cli update 99 "does not exist"
# Error: Task not found with id: 99

task-cli update abc "not a number"
# The id must be a whole number.

task-cli mark banana 1
# Error: Unknown status: banana

task-cli something
# Unknown command: something
```

## Data storage

Tasks are saved to a file named `tasks.json` in the current working directory. It is created automatically the first time a task is saved, and it is intentionally excluded from version control.

Each task records five fields:

| Field | Description |
| --- | --- |
| `id` | Unique identifier, assigned automatically |
| `description` | The task text |
| `status` | One of `todo`, `in-progress`, `done` |
| `createdAt` | When the task was created |
| `updatedAt` | When the task was last modified |

Example file contents:

```json
[ {
  "id" : 1,
  "description" : "Buy groceries and cook dinner",
  "status" : "todo",
  "createdAt" : "2026-07-16T22:11:22.686863",
  "updatedAt" : "2026-07-16T22:11:49.785791"
}, {
  "id" : 2,
  "description" : "Write report",
  "status" : "in-progress",
  "createdAt" : "2026-07-16T22:11:26.551967",
  "updatedAt" : "2026-07-16T22:11:26.553573"
} ]
```

## Project structure

The application follows a layered architecture. Each layer has a single responsibility and depends only on the layer directly beneath it — dependencies point downwards, never upwards.

```
src/main/java/com/diogo/tasktracker/
├── TaskTrackerApplication.java     # Entry point — boots the Spring context
├── cli/
│   └── TaskCliRunner.java          # Presentation layer: parses arguments, prints output
├── service/
│   └── TaskService.java            # Business layer: the task operations and rules
├── repository/
│   └── TaskRepository.java         # Persistence layer: reads and writes tasks.json
├── model/
│   ├── Task.java                   # The task itself
│   └── Status.java                 # Allowed statuses (todo, in-progress, done)
└── config/
    └── JacksonConfig.java          # Configures the JSON mapper
```

| Layer | Responsibility |
| --- | --- |
| Presentation (`cli`) | Reads command-line arguments and displays results. The only layer that touches the terminal. |
| Business (`service`) | Implements the task operations. Knows nothing about files or the terminal. |
| Persistence (`repository`) | The only layer that reads from and writes to the JSON file. |
| Model | The data that flows through every layer. |

Because the persistence layer is isolated, the JSON file could be swapped for a database by changing a single class — and the same service could be reused behind a REST API without altering any business logic.

## Running tests

```bash
./mvnw test
```

The test suite uses Mockito to replace the repository with a mock, so the service layer is tested in isolation without touching the filesystem.

## Design notes

The specification this project was based on suggests two separate commands, `mark-in-progress <id>` and `mark-done <id>`. This implementation instead provides a single, more flexible command — `mark <status> <id>` — which supports every status, including moving a task back to `todo`.
