package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.CategoryStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Venue;
import com.lufthansa.internship.entity.VenueStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
    List<Venue> findByCity(String city);

    Venue findByName(@NotBlank String name);

    @Query("SELECT v.status FROM Venue v WHERE v.id = :venueId")
    VenueStatus findStatusById(@Param("venueId") Integer venueId);

}
