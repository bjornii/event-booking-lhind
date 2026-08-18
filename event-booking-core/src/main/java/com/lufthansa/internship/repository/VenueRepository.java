package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
    List<Venue> findByCity(String city);
}
