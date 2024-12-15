package com.example.dailycheckin.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dailycheckin.model.CheckIn;
import com.example.dailycheckin.model.PointHistory;
import com.example.dailycheckin.model.User;
import com.example.dailycheckin.repository.CheckInRepository;
import com.example.dailycheckin.repository.UserRepository;

@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Autowired
    private RedissonClient redissonClient;

    private static final String REDIS_LOCK_KEY = "checkin-lock:";
    private static final String CHECKIN_CACHE_PREFIX = "checkin-history:";

    /**
     * Lấy trạng thái điểm danh của người dùng.
     */
    public List<CheckIn> getCheckInStatus(Long userId) {
        return checkInRepository.findByUserId(userId);
    }

    /**
     * Điểm danh với cơ chế Redis Lock.
     */
    @Transactional
    public String checkIn(Long userId) {
        String lockKey = REDIS_LOCK_KEY + userId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();

                // Kiểm tra khung giờ hợp lệ
                if (!((now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(11, 0)))
                        || (now.isAfter(LocalTime.of(19, 0)) && now.isBefore(LocalTime.of(21, 0))))) {
                    return "Ngoài khung giờ điểm danh!";
                }

                // Kiểm tra nếu đã điểm danh
                if (checkInRepository.existsByUserIdAndCheckInDate(userId, today)) {
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
                int points = 10;
                user.setLotusPoints(user.getLotusPoints() + points);
                userRepository.save(user);

                PointHistory pointHistory = new PointHistory();
                pointHistory.setUser(user);
                pointHistory.setPoints(points);
                pointHistory.setTimestamp(java.time.LocalDateTime.now());
                pointHistoryService.savePointHistory(pointHistory);

                // Xóa cache sau khi cập nhật dữ liệu
                clearCache(userId);

                return "Điểm danh thành công!";
            } else {
                return "Hệ thống đang bận, vui lòng thử lại!";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lỗi khi xử lý điểm danh!", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Phân trang lịch sử điểm danh với cache.
     */
    public Page<CheckIn> getCheckInHistory(Long userId, int page, int size) {
        String cacheKey = CHECKIN_CACHE_PREFIX + userId + ":page:" + page;

        // Kiểm tra cache
        Page<CheckIn> cachedPage = (Page<CheckIn>) redissonClient.getBucket(cacheKey).get();
        if (cachedPage != null) {
            return cachedPage;
        }

        // Nếu không có trong cache, truy vấn cơ sở dữ liệu
        Pageable pageable = PageRequest.of(page, size);
        Page<CheckIn> checkInPage = checkInRepository.findByUserId(userId, pageable);

        // Lưu kết quả vào cache
        redissonClient.getBucket(cacheKey).set(checkInPage, 10, TimeUnit.MINUTES);

        return checkInPage;
    }

    /**
     * Xóa cache của người dùng.
     */
    public void clearCache(Long userId) {
        String cacheKeyPrefix = CHECKIN_CACHE_PREFIX + userId;
        redissonClient.getKeys().deleteByPattern(cacheKeyPrefix + "*");
    }
}
