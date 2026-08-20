package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.EventVisibility;
import com.lufthansa.internship.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findByTitle(String title);


    Page<Event> findByVenueIn(List<Venue> cityVenues, Pageable pageable);

    Page<Event> findByStartDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    Page<Event> findByPriceBetween(Double startPrice, Double endPrice, Pageable pageable);

    List<Event> findByOrganizerId(Integer userId);

    boolean existsEventByTitle(String title);

    Page<Event> findAllByVisibility(EventVisibility visibility, Pageable pageable);

    Page<Event> findByVisibilityAndCategories_NameIn(EventVisibility visibility, Collection<String> categoryNames, Pageable pageable);

    Page<Event> findByVisibilityAndVenueIn(EventVisibility eventVisibility, List<Venue> cityVenues, Pageable pageable);

    Page<Event> findByVisibilityAndStartDateTimeBetween(EventVisibility eventVisibility, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    Page<Event> findByVisibilityAndPriceBetween(EventVisibility eventVisibility, Double startPrice, Double endPrice, Pageable pageable);

    Optional<Event> findByVisibilityAndId(EventVisibility eventVisibility, Integer eventId);
}
