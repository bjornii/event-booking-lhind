package com.lufthansa.internship.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    @Column(name = "start_date_time")
    private LocalDate startDateTime;

    @Column(name = "end_date_time")
    private LocalDate endDateTime;

    private Double price;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private EventStatus status;


}
