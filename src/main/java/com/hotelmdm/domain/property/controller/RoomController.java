package com.hotelmdm.domain.property.controller;

import com.hotelmdm.domain.property.model.Room;
import com.hotelmdm.domain.property.model.RoomType;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.domain.property.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/property/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final HotelRepository hotelRepository;

    @GetMapping
    public String list(@RequestParam(required = false) Long hotelId, Model model) {
        if (hotelId != null) {
            model.addAttribute("rooms", roomService.findByHotelId(hotelId));
            hotelRepository.findById(hotelId).ifPresent(h -> model.addAttribute("selectedHotel", h));
        } else {
            model.addAttribute("rooms", roomService.findAll());
        }
        model.addAttribute("hotels", hotelRepository.findAllByOrderByNameAsc());
        return "property/rooms/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String newRoom(@RequestParam(required = false) Long hotelId, Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("hotels", hotelRepository.findAllByOrderByNameAsc());
        model.addAttribute("preselectedHotelId", hotelId);
        return "property/rooms/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String createRoom(@Valid @ModelAttribute("room") Room room,
                             BindingResult result,
                             @RequestParam Long hotelId,
                             Authentication auth,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("hotels", hotelRepository.findAllByOrderByNameAsc());
            return "property/rooms/form";
        }
        Room saved = roomService.save(room, hotelId, auth.getName());
        ra.addFlashAttribute("successMessage", "Room " + saved.getRoomNumber() + " created.");
        return "redirect:/property/hotels/" + hotelId;
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String editRoom(@PathVariable Long id, Model model) {
        Room room = roomService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("hotels", hotelRepository.findAllByOrderByNameAsc());
        model.addAttribute("preselectedHotelId", room.getHotel().getId());
        return "property/rooms/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String updateRoom(@PathVariable Long id,
                             @Valid @ModelAttribute("room") Room room,
                             BindingResult result,
                             @RequestParam Long hotelId,
                             Authentication auth,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypes", RoomType.values());
            model.addAttribute("hotels", hotelRepository.findAllByOrderByNameAsc());
            return "property/rooms/form";
        }
        room.setId(id);
        roomService.save(room, hotelId, auth.getName());
        ra.addFlashAttribute("successMessage", "Room updated.");
        return "redirect:/property/hotels/" + hotelId;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteRoom(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Room room = roomService.findById(id).orElseThrow();
        Long hotelId = room.getHotel().getId();
        roomService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Room deleted.");
        return "redirect:/property/hotels/" + hotelId;
    }
}
