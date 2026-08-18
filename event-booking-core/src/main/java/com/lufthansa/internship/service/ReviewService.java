package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.ReviewRequestDTO;
import com.lufthansa.internship.dto.response.ReviewResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.EventNotFoundException;
import com.lufthansa.internship.exception.InvalidReviewRequestException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.mapper.ReviewMapper;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.ReviewRepository;
import com.lufthansa.internship.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponseDTO leaveReview(ReviewRequestDTO request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User does not exist"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new EventNotFoundException("Event does not exist"));

        boolean hasBooking = bookingRepository.existsByUserAndEventAndStatus(user, event, BookingStatus.CONFIRMED);
        if (!hasBooking) throw new InvalidReviewRequestException("User does not have booking, cannot leave review");
        boolean hasReview = reviewRepository.existsByUserAndEvent(user, event);
        if (hasReview) throw new InvalidReviewRequestException("User already left a review for this event");

        if (event.getStatus() != EventStatus.FINISHED) throw new InvalidReviewRequestException("Cannot leave review before event ends");
        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setEvent(event);
        review.setCreatedAt(LocalDateTime.now());


        Review saved = reviewRepository.save(review);
        return reviewMapper.toDTO(saved);
    }

}
