package com.hotelmdm.governance.service;

import com.hotelmdm.governance.model.DataStewardTask;
import com.hotelmdm.governance.model.TaskStatus;
import com.hotelmdm.governance.repository.DataStewardTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataStewardTaskService {

    private final DataStewardTaskRepository taskRepository;

    public DataStewardTask save(DataStewardTask task) {
        return taskRepository.save(task);
    }

    public Optional<DataStewardTask> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<DataStewardTask> findAll() {
        return taskRepository.findAllByOrderByDueDateAsc();
    }

    public List<DataStewardTask> findByStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByDueDateAsc(status);
    }

    @Transactional
    public DataStewardTask updateStatus(Long id, TaskStatus status) {
        DataStewardTask task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public long countOpen() {
        return taskRepository.countByStatus(TaskStatus.OPEN);
    }
}
