package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.ReviewRequestDTO;
import com.lufthansa.internship.dto.response.ReviewResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.EventNotFoundException;
import com.lufthansa.internship.exception.InvalidReviewRequestException;
import com.lufthansa.internship.exception.ReviewNotFoundException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.mapper.ReviewMapper;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.ReviewRepository;
import com.lufthansa.internship.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponseDTO leaveReview(Integer userId, ReviewRequestDTO request){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not exist"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new EventNotFoundException("Event does not exist"));

        boolean hasBooking = bookingRepository.existsByUserAndEventAndStatus(user, event, BookingStatus.CONFIRMED);
        if (!hasBooking) {
            logger.warn("Review rejected: user {} has no confirmed booking for event {}", userId, event.getId());
            throw new InvalidReviewRequestException("User does not have booking, cannot leave review");
        }
        boolean hasReview = reviewRepository.existsByUserAndEvent(user, event);
        if (hasReview) {
            logger.warn("Review rejected: user {} already reviewed event {}", userId, event.getId());
            throw new InvalidReviewRequestException("User already left a review for this event");
        }
        if (event.getStatus() != EventStatus.FINISHED) {
            logger.warn("Review rejected: event {} has not finished yet", event.getId());
            throw new InvalidReviewRequestException("Cannot leave review before event ends");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setEvent(event);
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        logger.info("Review {} created by user {} for event {}", saved.getId(), userId, event.getId());
        return reviewMapper.toDTO(saved);
    }

    @Transactional
    public void deleteReview(Integer reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewRepository.delete(review);
        logger.info("Review {} deleted", reviewId);
    }
}