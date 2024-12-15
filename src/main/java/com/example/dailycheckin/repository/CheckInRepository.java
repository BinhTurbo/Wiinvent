package com.example.dailycheckin.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dailycheckin.model.CheckIn;

import jakarta.persistence.LockModeType;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    List<CheckIn> findByUserId(Long userId);
    Page<CheckIn> findByUserId(Long userId, Pageable pageable);
    CheckIn findByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
    boolean existsByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckIn c WHERE c.user.id = :userId AND c.checkInDate = :checkInDate")
    CheckIn findWithLockByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
}
