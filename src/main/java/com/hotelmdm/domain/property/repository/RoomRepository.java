package com.hotelmdm.domain.property.repository;

import com.hotelmdm.domain.property.model.Room;
import com.hotelmdm.domain.property.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdOrderByRoomNumberAsc(Long hotelId);
    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
    boolean existsByHotelIdAndRoomNumberAndIdNot(Long hotelId, String roomNumber, Long id);
    long countByHotelId(Long hotelId);
    List<Room> findByAvailableTrueOrderByHotelNameAscRoomNumberAsc();
}
