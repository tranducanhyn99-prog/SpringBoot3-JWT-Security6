# SpringBoot3-JWT-Security6

Dự án Demo xác thực và phân quyền bằng **JSON Web Token (JWT)** trên nền tảng **Spring Boot 3** và **Spring Security 6**, kết hợp giao diện Web Client tương tác bằng **AJAX**.

Bài tập thực hành Tuần 5 - Môn: **Lập Trình Web (WEBPR330479)** - GV: **ThS. Nguyễn Hữu Trung** (HCMUTE).

---

## 📌 Công nghệ sử dụng
- **Java**: 21
- **Spring Boot**: 3.3.4
- **Spring Security**: 6.x (Kiến trúc Stateless Session, SecurityFilterChain, OncePerRequestFilter)
- **JJWT**: 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **Spring Data JPA & Hibernate**
- **Cơ sở dữ liệu**:
  - H2 Database (In-Memory, sẵn sàng chạy ngay không cần cài đặt)
  - Microsoft SQL Server (Hỗ trợ cấu hình chuyển đổi nhanh)
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery AJAX

---

## 📂 Cấu trúc dự án
```text
bt tuan 5/
├── src/
│   ├── main/
│   │   ├── java/vn/iotstar/
│   │   │   ├── configs/
│   │   │   │   ├── ApplicationConfiguration.java   # Cấu hình Bean UserDetailsService, BCrypt, AuthProvider
│   │   │   │   └── SecurityConfiguration.java      # Cấu hình SecurityFilterChain, Stateless, CORS, Filter
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java             # Điều hướng View Thymeleaf (/login, /user/profile)
│   │   │   │   ├── AuthenticationController.java   # REST API xác thực (/auth/signup, /auth/login)
│   │   │   │   └── UserController.java             # REST API người dùng (/users/me, /users/)
│   │   │   ├── entity/
│   │   │   │   └── User.java                       # Entity User implements UserDetails
│   │   │   ├── exceptions/
│   │   │   │   └── GlobalExceptionHandler.java     # Xử lý ngoại lệ bảo mật & trả về ProblemDetail
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java    # Bộ lọc bắt Authorization: Bearer <token>
│   │   │   ├── models/
│   │   │   │   ├── LoginResponse.java              # DTO phản hồi chứa token và expiresIn
│   │   │   │   ├── LoginUserModel.java             # DTO nhận đăng nhập
│   │   │   │   └── RegisterUserModel.java          # DTO nhận đăng ký
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java             # Spring Data JPA Repository
│   │   │   ├── services/
│   │   │   │   ├── AuthenticationService.java      # Logic đăng ký & xác thực tài khoản
│   │   │   │   ├── JwtService.java                 # Logic sinh mã, giải mã & kiểm tra JWT
│   │   │   │   └── UserService.java                # Logic nghiệp vụ quản lý người dùng
│   │   │   └── JwtSpringboot3Application.java      # Lớp khởi chạy ứng dụng
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── images/
│   │       │   │   └── default-avatar.png
│   │       │   └── js/
│   │       │       └── mainjs.js                   # Mã AJAX login, lưu localStorage, gửi Bearer token
│   │       ├── templates/
│   │       │   ├── login.html                      # Trang đăng nhập giao diện AJAX
│   │       │   └── profile.html                    # Trang thông tin cá nhân
│   │       ├── application.properties              # Cấu hình chính (Port 8005, JWT Secret, Expiration)
│   │       ├── application-h2.properties           # Cấu hình H2 Database
│   │       └── application-sqlserver.properties    # Cấu hình Microsoft SQL Server
│   └── test/
│       └── java/vn/iotstar/
│           └── JwtSpringboot3ApplicationTests.java # Kiểm thử tích hợp tự động toàn bộ luồng JWT
├── pom.xml
└── README.md
```

---

## 🚀 Hướng dẫn chạy ứng dụng

### 1. Chạy với H2 Database (Mặc định)
Dự án được cấu hình mặc định sử dụng H2 Database (`spring.profiles.active=h2`). Bạn chỉ cần chạy lệnh sau mà không cần cài đặt hoặc cấu hình thêm bất kỳ CSDL nào:
```bash
mvn spring-boot:run
```
- Server chạy tại: `http://localhost:8005`
- H2 Console tại: `http://localhost:8005/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:jwt_db`
  - **User Name**: `sa`
  - **Password**: *(để trống)*

### 2. Chuyển sang Microsoft SQL Server
Mở file `src/main/resources/application.properties` và đổi:
```properties
spring.profiles.active=sqlserver
```
Và kiểm tra thông tin đăng nhập trong file `src/main/resources/application-sqlserver.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=jwt_db;encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2;characterEncoding=UTF-8
spring.datasource.username=sa
spring.datasource.password=123456
```

---

## 🧪 Hướng dẫn kiểm thử

### Cách 1: Sử dụng Giao diện Web Client (AJAX)
1. Mở trình duyệt và truy cập: [http://localhost:8005/login](http://localhost:8005/login)
2. Nhập thông tin đăng nhập (hoặc dùng Postman tạo tài khoản trước).
3. Sau khi bấm **Login**, AJAX sẽ gửi request `POST /auth/login`, nhận JWT token và lưu vào `localStorage.token`.
4. Trình duyệt tự động chuyển hướng sang trang [http://localhost:8005/user/profile](http://localhost:8005/user/profile). Trang này tự động gọi API `/users/me` kèm header `Authorization: Bearer <token>` để hiển thị thông tin người dùng.
5. Bấm nút **Logout** để xóa token khỏi `localStorage` và quay về trang đăng nhập.

### Cách 2: Sử dụng Postman

#### 1. Đăng ký tài khoản mới (`POST`)
- **URL**: `http://localhost:8005/auth/signup`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "email": "trungnh@hcmute.edu.vn",
    "password": "password123",
    "fullName": "Nguyen Huu Trung"
  }
  ```

#### 2. Đăng nhập lấy JWT Token (`POST`)
- **URL**: `http://localhost:8005/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "email": "trungnh@hcmute.edu.vn",
    "password": "password123"
  }
  ```
- **Response trả về**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 3600000
  }
  ```

#### 3. Truy cập Endpoint bảo vệ (`GET`)
- **URL**: `http://localhost:8005/users/me` hoặc `http://localhost:8005/users/`
- **Headers**:
  - `Authorization`: `Bearer <chuỗi_token_vừa_lấy_ở_bước_2>`
- **Response**: Trả về thông tin chi tiết người dùng được trích xuất từ JWT context.

---

## 🛡️ Xử lý ngoại lệ bảo mật
Dự án cài đặt `GlobalExceptionHandler` (`@RestControllerAdvice`) xử lý các mã lỗi:
- `401 Unauthorized`: Sai thông tin đăng nhập (`BadCredentialsException`), token hết hạn (`ExpiredJwtException`), token sai chữ ký (`SignatureException`).
- `403 Forbidden`: Tài khoản bị khóa (`AccountStatusException`), không có quyền truy cập (`AccessDeniedException`).
