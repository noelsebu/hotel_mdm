package com.hotelmdm.governance.controller;

import com.hotelmdm.governance.model.DataStewardTask;
import com.hotelmdm.governance.model.TaskPriority;
import com.hotelmdm.governance.model.TaskStatus;
import com.hotelmdm.governance.service.DataStewardTaskService;
import com.hotelmdm.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/governance/tasks")
@RequiredArgsConstructor
public class DataStewardTaskController {

    private final DataStewardTaskService taskService;
    private final UserRepository userRepository;

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            model.addAttribute("tasks", taskService.findByStatus(TaskStatus.valueOf(status)));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("tasks", taskService.findAll());
        }
        model.addAttribute("statuses", TaskStatus.values());
        return "governance/tasks/list";
    }

    @GetMapping("/new")
    public String newTask(Model model) {
        model.addAttribute("task", new DataStewardTask());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("stewards", userRepository.findAll());
        return "governance/tasks/form";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") DataStewardTask task,
                             BindingResult result,
                             Authentication auth,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("stewards", userRepository.findAll());
            return "governance/tasks/form";
        }
        task.setCreatedBy(auth.getName());
        taskService.save(task);
        ra.addFlashAttribute("successMessage", "Task created successfully.");
        return "redirect:/governance/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTask(@PathVariable Long id, Model model) {
        DataStewardTask task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        model.addAttribute("task", task);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("stewards", userRepository.findAll());
        return "governance/tasks/form";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("task") DataStewardTask task,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("stewards", userRepository.findAll());
            return "governance/tasks/form";
        }
        task.setId(id);
        taskService.save(task);
        ra.addFlashAttribute("successMessage", "Task updated.");
        return "redirect:/governance/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TaskStatus status,
                               RedirectAttributes ra) {
        taskService.updateStatus(id, status);
        ra.addFlashAttribute("successMessage", "Task status updated to " + status.getLabel());
        return "redirect:/governance/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        taskService.delete(id);
        ra.addFlashAttribute("successMessage", "Task deleted.");
        return "redirect:/governance/tasks";
    }
}
