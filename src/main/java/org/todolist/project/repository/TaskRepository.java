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

    List<Task> findByStatusId(Long statusId);

    List<Task> findByPriority(Priority priority);

    List<Task> findByStatusIdAndPriority(Long statusId, Priority priority);

    List<Task> findByDueDateBefore(LocalDate date);

    List<Task> findByDueDateAfter(LocalDate date);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findAllByOrderByPriorityDesc();

    List<Task> findAllByOrderByDueDateAsc();

    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.statusId != :completedStatusId")
    List<Task> findOverdueTasks(Long completedStatusId);

    Long countByStatusId(Long statusId);

    Long countByPriority(Priority priority);
}
