package org.todolist.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.todolist.project.model.Subtask;

import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByTaskIdOrderByPositionAsc(Long taskId);

    List<Subtask> findByTaskIdAndIsCompletedTrue(Long taskId);

    List<Subtask> findByTaskIdAndIsCompletedFalse(Long taskId);

    Long countByTaskId(Long taskId);

    Long countByTaskIdAndIsCompletedTrue(Long taskId);

    Long countByTaskIdAndIsCompletedFalse(Long taskId);


    void deleteByTaskId(Long taskId);

    @Query("SELECT " +
            "COUNT(s) as total, " +
            "SUM(CASE WHEN s.isCompleted = true THEN 1 ELSE 0 END) as completed " +
            "FROM Subtask s WHERE s.taskId = :taskId")
    Object getTaskProgress(Long taskId);
}
