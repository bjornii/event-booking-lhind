package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findByTitle(String title);

    Page<Event> findByCategoriesIn(List<String> categories, Pageable pageable);

    Page<Event> findByVenueIn(List<Venue> cityVenues, Pageable pageable);

    Page<Event> findByStartDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    Page<Event> findByPriceBetween(Double startPrice, Double endPrice, Pageable pageable);
}
