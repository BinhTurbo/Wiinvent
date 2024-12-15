package com.example.dailycheckin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dailycheckin.model.CheckIn;
import com.example.dailycheckin.service.CheckInService;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    /**
     * Lấy trạng thái điểm danh.
     */
    @GetMapping("/{userId}")
    public List<CheckIn> getCheckInStatus(@PathVariable Long userId) {
        return checkInService.getCheckInStatus(userId);
    }

    /**
     * Điểm danh.
     */
    @PostMapping("/{userId}")
    public String checkIn(@PathVariable Long userId) {
        return checkInService.checkIn(userId);
    }

    /**
     * Phân trang lịch sử điểm danh.
     */
    @GetMapping("/{userId}/history")
    public Page<CheckIn> getCheckInHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return checkInService.getCheckInHistory(userId, page, size);
    }

    /**
     * Xóa cache.
     */
    @DeleteMapping("/{userId}/cache")
    public String clearCache(@PathVariable Long userId) {
        checkInService.clearCache(userId);
        return "Cache đã được xóa cho userId: " + userId;
    }
}
