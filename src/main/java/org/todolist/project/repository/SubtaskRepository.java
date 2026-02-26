package org.todolist.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.todolist.project.model.Subtask;

import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    // Найти все подзадачи для конкретной задачи (отсортированные по position)
    List<Subtask> findByTaskIdOrderByPositionAsc(Long taskId);

    // Найти все выполненные подзадачи для задачи
    List<Subtask> findByTaskIdAndIsCompletedTrue(Long taskId);

    // Найти все невыполненные подзадачи для задачи
    List<Subtask> findByTaskIdAndIsCompletedFalse(Long taskId);

    // Подсчитать общее количество подзадач для задачи
    Long countByTaskId(Long taskId);

    // Подсчитать выполненные подзадачи для задачи
    Long countByTaskIdAndIsCompletedTrue(Long taskId);

    // Подсчитать невыполненные подзадачи
    Long countByTaskIdAndIsCompletedFalse(Long taskId);

    // Удалить все подзадачи для задачи
    void deleteByTaskId(Long taskId);

    // Получить прогресс задачи (кастомный запрос)
    @Query("SELECT " +
            "COUNT(s) as total, " +
            "SUM(CASE WHEN s.isCompleted = true THEN 1 ELSE 0 END) as completed " +
            "FROM Subtask s WHERE s.taskId = :taskId")
    Object getTaskProgress(Long taskId);
}
