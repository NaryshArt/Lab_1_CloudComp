package org.todolist.project.repository;

import org.springframework.transaction.annotation.Transactional;
import org.todolist.project.model.Task;
import org.todolist.project.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Найти все задачи по статусу
    List<Task> findByStatusId(Long statusId);

    // Найти все задачи по приоритету
    List<Task> findByPriority(Priority priority);

    // Найти задачи по статусу и приоритету
    List<Task> findByStatusIdAndPriority(Long statusId, Priority priority);

    // Найти задачи с дедлайном до определённой даты
    List<Task> findByDueDateBefore(LocalDate date);

    // Найти задачи с дедлайном после определённой даты
    List<Task> findByDueDateAfter(LocalDate date);

    // Найти задачи по части названия (поиск)
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Сортировка по дате создания (новые первыми)
    List<Task> findAllByOrderByCreatedAtDesc();

    // Сортировка по приоритету
    List<Task> findAllByOrderByPriorityDesc();

    // Сортировка по дедлайну
    List<Task> findAllByOrderByDueDateAsc();

    // Найти просроченные задачи (дедлайн прошёл, но статус != "Выполнено")
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.statusId != :completedStatusId")
    List<Task> findOverdueTasks(Long completedStatusId);

    // Подсчёт задач по статусу
    Long countByStatusId(Long statusId);

    // Подсчёт задач по приоритету
    Long countByPriority(Priority priority);
}
