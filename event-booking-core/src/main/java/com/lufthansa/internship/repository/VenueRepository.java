package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Venue;
import com.lufthansa.internship.entity.VenueStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
    List<Venue> findByCity(String city);

    Venue findByName(@NotBlank String name);

    @Query("SELECT v.status FROM Venue v WHERE v.id = :venueId")
    VenueStatus findStatusById(@Param("venueId") Integer venueId);

    List<Venue> findByCityIgnoreCase(String city);

    Optional<Venue> findByIdAndStatus(Integer venueId, VenueStatus venueStatus);
}
