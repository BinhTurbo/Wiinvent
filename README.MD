# **Daily Check-In System**

A simple daily check-in system built with **Spring Boot** to manage user check-ins and reward points based on specific time windows. This project supports database transactions and caching with Redis for high performance.
## **Features**
- User registration and profile management.
- Daily check-in functionality with time window restrictions:
  - Morning: `9:00 AM - 11:00 AM`
  - Evening: `7:00 PM - 9:00 PM`
- Automatic reward point allocation upon successful check-in.
- Caching with Redis for fast data retrieval.
- Full transaction management to ensure data consistency.
- API endpoints for frontend or external integrations.
## **Tech Stack**
- **Backend**: Java, Spring Boot
- **Database**: MySQL
- **Cache**: Redis
- **Build Tool**: Maven
- **API Documentation**: Postman
- **UI Framework (Optional)**: Thymeleaf
## **Installation**

### **1. Clone the Repository**
```bash
git clone https://github.com/yourusername/daily-check-in.git
cd daily-check-in
2. Prerequisites
* Java: JDK 17 or higher
* MySQL: Installed and running
* Redis: Installed and running on default port 6379
3. Configure the Application
* Edit the src/main/resources/application.properties file to set up the database and Redis configuration:
# MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/daily_checkin
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
4. Build and Run

---


```markdown
## **API Endpoints**

### **1. User Management**
#### **1.1. Create User**
- **Method**: `POST`
- **URL**: `/api/users`
- **Body**:
  ```json
  {
    "name": "John Doe",
    "avatar": "https://example.com/avatar.jpg"
  }
* Response
{
  "id": 1,
  "name": "John Doe",
  "avatar": "https://example.com/avatar.jpg",
  "lotusPoints": 0,
  "checkIns": []
}

---

### **Check-In Management**

```markdown
### **2. Check-In Management**
#### **2.1. Check-In**
- **Method**: `POST`
- **URL**: `/api/checkins/{userId}`
- **Response**:
  - If successful:
    ```json
    "Điểm danh thành công!"
    ```
  - If outside the time window:
    ```json
    "Ngoài khung giờ điểm danh!"
    ```
  - If already checked in:
    ```json
    "Bạn đã điểm danh hôm nay!"
    ```

#### **2.2. Get Check-In Status**
- **Method**: `GET`
- **URL**: `/api/checkins/{userId}`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "checkInDate": "2024-12-13",
      "checkedIn": true,
      "user": {
        "id": 1,
        "name": "John Doe"
      }
    }
  ]

---

### **Reward Points History**

```markdown
### **3. Reward Points History**
#### **3.1. Get Points History**
- **Method**: `GET`
- **URL**: `/api/points-history/{userId}`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "userId": 1,
      "points": 10,
      "timestamp": "2024-12-13T09:15:00"
    }
  ]

---

### **Database Schema**

```markdown
## **Database Schema**

### **1. User Table**
| Column        | Type          | Description           |
|---------------|---------------|-----------------------|
| id            | Long (PK)     | User ID              |
| name          | String        | User name            |
| avatar        | String        | Profile picture URL  |
| lotus_points  | Integer       | Accumulated points   |

### **2. CheckIn Table**
| Column         | Type          | Description                   |
|----------------|---------------|-------------------------------|
| id             | Long (PK)     | Check-in ID                  |
| check_in_date  | LocalDate     | Date of check-in             |
| checked_in     | Boolean       | Status (checked-in or not)   |
| user_id        | Long (FK)     | Associated user ID           |

### **3. PointHistory Table**
| Column      | Type          | Description                 |
|-------------|---------------|-----------------------------|
| id          | Long (PK)     | History ID                 |
| user_id     | Long (FK)     | Associated user ID         |
| points      | Integer       | Points rewarded            |
| timestamp   | LocalDateTime | Time of point allocation   |
## **Caching with Redis**
- **Key Format**: `checkin:{userId}`
- **Value**: List of Check-In records for the user.
- **Use Cases**:
  - Cache check-in status for faster retrieval.
  - Clear and update cache after successful check-in.
## **Development and Testing**

### **1. Development Environment**
- Use **Postman** to test all API endpoints.
- Use MySQL Workbench to manage database and data.

### **2. Testing Check-In Logic**
1. Test point allocation:
   - Ensure points are added correctly upon each check-in.
2. Test time window restrictions:
   - Verify that check-in fails outside the defined time windows.
3. Test Redis cache:
   - Verify that check-in status is cached and updated properly.
## **Contributors**
- **BonZFLame** - Developer
