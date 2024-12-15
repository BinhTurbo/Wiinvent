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

    /**
     * Tìm tất cả các bản ghi CheckIn theo userId.
     */
    List<CheckIn> findByUserId(Long userId);

    /**
     * Tìm tất cả các bản ghi CheckIn theo userId với phân trang.
     */
    Page<CheckIn> findByUserId(Long userId, Pageable pageable);

    /**
     * Tìm một bản ghi CheckIn theo userId và ngày điểm danh.
     */
    CheckIn findByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);

    /**
     * Kiểm tra xem có tồn tại bản ghi CheckIn theo userId và ngày điểm danh
     * không.
     */
    boolean existsByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);

    /**
     * Tìm một bản ghi CheckIn với Pessimistic Lock để ngăn truy cập đồng thời.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CheckIn c WHERE c.user.id = :userId AND c.checkInDate = :checkInDate")
    CheckIn findWithLockByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
}
