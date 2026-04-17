package com.hotelmdm.domain.property.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.property.model.Hotel;
import com.hotelmdm.domain.property.model.Room;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.domain.property.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final AuditService auditService;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findByHotelId(Long hotelId) {
        return roomRepository.findByHotelIdOrderByRoomNumberAsc(hotelId);
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Transactional
    public Room save(Room room, Long hotelId, String actor) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + hotelId));
        room.setHotel(hotel);

        boolean isNew = room.getId() == null;
        if (!isNew && roomRepository.existsByHotelIdAndRoomNumberAndIdNot(hotelId, room.getRoomNumber(), room.getId())) {
            throw new IllegalArgumentException("Room number already exists in this hotel");
        }
        Room saved = roomRepository.save(room);
        auditService.log(isNew ? "CREATE" : "UPDATE", "ROOM", saved.getId(), actor,
                "Room " + saved.getRoomNumber() + " [" + saved.getRoomType() + "] in " + hotel.getName());
        return saved;
    }

    public void delete(Long id, String actor) {
        roomRepository.findById(id).ifPresent(r -> {
            auditService.log("DELETE", "ROOM", id, actor,
                    "Room " + r.getRoomNumber() + " in " + r.getHotel().getName());
            roomRepository.deleteById(id);
        });
    }
}
