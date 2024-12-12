package com.example.dailycheckin.service;

import com.example.dailycheckin.model.CheckIn;
import com.example.dailycheckin.model.User;
import com.example.dailycheckin.repository.CheckInRepository;
import com.example.dailycheckin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CheckIn> getCheckInStatus(Long userId) {
        return checkInRepository.findByUserId(userId);
    }

    public String checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Kiểm tra khung giờ hợp lệ
        if (!((now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(11, 0)))
                || (now.isAfter(LocalTime.of(19, 0)) && now.isBefore(LocalTime.of(21, 0))))) {
            return "Ngoài khung giờ điểm danh!";
        }

        // Kiểm tra nếu đã điểm danh
        CheckIn existingCheckIn = checkInRepository.findByUserIdAndCheckInDate(userId, today);
        if (existingCheckIn != null && existingCheckIn.isCheckedIn()) {
            return "Bạn đã điểm danh hôm nay!";
        }

        // Thực hiện điểm danh
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setCheckInDate(today);
        checkIn.setCheckedIn(true);

        checkInRepository.save(checkIn);

        // Cộng điểm cho người dùng
        user.setLotusPoints(user.getLotusPoints() + 10);
        userRepository.save(user);

        return "Điểm danh thành công!";
    }
}
