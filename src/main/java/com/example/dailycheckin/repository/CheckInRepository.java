package com.example.dailycheckin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dailycheckin.model.CheckIn;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByUserId(Long userId);
    CheckIn findByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
}
