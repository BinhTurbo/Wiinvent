package com.example.dailycheckin.service;

import com.example.dailycheckin.model.PointHistory;
import com.example.dailycheckin.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    public List<PointHistory> getPointsHistory(Long userId) {
        return pointHistoryRepository.findByUserId(userId);
    }

    public void savePointHistory(PointHistory pointHistory) {
        pointHistoryRepository.save(pointHistory);
    }
}
