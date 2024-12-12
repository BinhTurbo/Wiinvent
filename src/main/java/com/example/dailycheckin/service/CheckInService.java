package com.example.dailycheckin.service;

import com.example.dailycheckin.model.CheckIn;
import com.example.dailycheckin.model.PointHistory;
import com.example.dailycheckin.model.User;
import com.example.dailycheckin.repository.CheckInRepository;
import com.example.dailycheckin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CHECKIN_KEY_PREFIX = "checkin:";

    /**
     * Lấy danh sách trạng thái điểm danh từ Redis cache
     */
    public List<CheckIn> getCachedCheckInStatus(Long userId) {
        String key = CHECKIN_KEY_PREFIX + userId;
        return (List<CheckIn>) redisTemplate.opsForValue().get(key);
    }

    /**
     * Lưu trạng thái điểm danh vào Redis cache
     */
    public void cacheCheckInStatus(Long userId, List<CheckIn> checkInList) {
        String key = CHECKIN_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, checkInList);
    }

    /**
     * Xóa cache của người dùng
     */
    public void clearCache(Long userId) {
        String key = CHECKIN_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * Xử lý logic điểm danh với Transaction
     */
    @Transactional
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

        // Lấy thông tin người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Thực hiện điểm danh
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setCheckInDate(today);
        checkIn.setCheckedIn(true);
        checkInRepository.save(checkIn);

        // Cộng điểm và lưu lịch sử
        int points = 10; // Số điểm cố định
        user.setLotusPoints(user.getLotusPoints() + points);
        userRepository.save(user);

        // Lưu lịch sử cộng điểm
        PointHistory pointHistory = new PointHistory();
        pointHistory.setUser(user);
        pointHistory.setPoints(points);
        pointHistory.setTimestamp(LocalDateTime.now());
        pointHistoryService.savePointHistory(pointHistory);

        // Xóa cache cũ
        clearCache(userId);

        // Cập nhật cache mới
        List<CheckIn> updatedCheckIns = checkInRepository.findByUserId(userId);
        cacheCheckInStatus(userId, updatedCheckIns);

        return "Điểm danh thành công!";
    }

    /**
     * Lấy trạng thái điểm danh từ Redis hoặc database nếu cache không tồn tại
     */
    public List<CheckIn> getCheckInStatus(Long userId) {
        List<CheckIn> cachedCheckIns = getCachedCheckInStatus(userId);
        if (cachedCheckIns != null) {
            return cachedCheckIns;
        }

        List<CheckIn> checkIns = checkInRepository.findByUserId(userId);
        cacheCheckInStatus(userId, checkIns);
        return checkIns;
    }
}
