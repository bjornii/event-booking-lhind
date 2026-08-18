package com.lufthansa.internship.controller;


import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @GetMapping("/myBookings")
    public Page<BookingResponseDTO> getMyBookings(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @RequestParam(defaultValue = "bookingDate") String sortVariable) {

        return bookingService.viewMyBookings(principal.getId(), sortVariable);
    }

}
