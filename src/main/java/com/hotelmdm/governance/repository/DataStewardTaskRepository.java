package com.hotelmdm.governance.repository;

import com.hotelmdm.governance.model.DataStewardTask;
import com.hotelmdm.governance.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataStewardTaskRepository extends JpaRepository<DataStewardTask, Long> {
    List<DataStewardTask> findByAssignedToOrderByDueDateAsc(String assignedTo);
    List<DataStewardTask> findByStatusOrderByDueDateAsc(TaskStatus status);
    List<DataStewardTask> findAllByOrderByDueDateAsc();
    long countByStatus(TaskStatus status);
    long countByAssignedToAndStatus(String assignedTo, TaskStatus status);
}
