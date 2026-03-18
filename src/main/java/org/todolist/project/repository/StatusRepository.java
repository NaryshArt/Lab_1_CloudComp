package org.todolist.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.todolist.project.model.Status;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByName(String name);

    boolean existsByName(String name);
}
