package com.example.dailycheckin.controller;

import com.example.dailycheckin.model.PointHistory;
import com.example.dailycheckin.service.PointHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points-history")
public class PointHistoryController {

    @Autowired
    private PointHistoryService pointHistoryService;

    @GetMapping("/{userId}")
    public List<PointHistory> getPointsHistory(@PathVariable Long userId) {
        return pointHistoryService.getPointsHistory(userId);
    }
}
