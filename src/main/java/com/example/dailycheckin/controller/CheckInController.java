package com.example.dailycheckin.controller;

import com.example.dailycheckin.service.CheckInService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.dailycheckin.model.CheckIn;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/{userId}")
    public List<CheckIn> getCheckInStatus(@PathVariable Long userId) {
        return checkInService.getCheckInStatus(userId);
    }

    @PostMapping("/{userId}")
    public String checkIn(@PathVariable Long userId) {
        return checkInService.checkIn(userId);
    }
}
