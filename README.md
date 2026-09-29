# SpringBoot3-JWT-Security6

Dự án hoàn chỉnh về cơ chế xác thực và phân quyền bằng **JSON Web Token (JWT)** trên nền tảng **Spring Boot 3** và **Spring Security 6**, kết hợp giao diện Web Client tương tác bằng **AJAX**.

Bài tập thực hành Tuần 5 - Môn: **Lập Trình Web (WEBPR330479)** - GV: **ThS. Nguyễn Hữu Trung** (HCMUTE).

---

## 📌 Công nghệ sử dụng
- **Java**: 21 LTS
- **Spring Boot**: 3.3.4
- **Spring Security**: 6.x (Kiến trúc Stateless Session, SecurityFilterChain, OncePerRequestFilter)
- **Thư viện JWT**: **Nimbus JOSE + JWT** (`com.nimbusds:nimbus-jose-jwt:9.40`)
- **Spring Data JPA & Hibernate**
- **Cơ sở dữ liệu**:
  - **H2 Database**: In-Memory (mặc định kích hoạt, chạy ngay không cần cài đặt)
  - **Microsoft SQL Server**: Hỗ trợ chuyển đổi nhanh qua profile `sqlserver`
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery AJAX

---

## 🚀 Đầy đủ tính năng 2 phần (Backend REST API & Frontend AJAX)

### 1. Phần Backend (REST API)
- `POST /auth/signup`: Đăng ký người dùng mới (mã hóa mật khẩu bằng BCrypt, lưu trữ CSDL).
- `POST /auth/login`: Xác thực thông tin qua `AuthenticationManager`, sinh chuỗi mã thông báo JWT chứa các claims và chữ ký bí mật.
- `GET /users/me`: Yêu cầu header `Authorization: Bearer <token>`, trích xuất thông tin người dùng hiện tại đang đăng nhập từ `SecurityContextHolder`.
- `GET /users/`: Yêu cầu JWT hợp lệ, trả về danh sách tất cả người dùng trong hệ thống.
- `@RestControllerAdvice (GlobalExceptionHandler)`: Bắt các ngoại lệ bảo mật và ném lỗi theo định dạng RFC 7807 `ProblemDetail` (401 BadCredentials, 401 ExpiredJwt, 401 Signature, 403 AccessDenied, 403 AccountStatus).

### 2. Phần Frontend (Giao diện Web AJAX)
- **Trang Đăng ký (`/register`)**: Form nhập Họ tên, Email, Mật khẩu, Xác nhận mật khẩu $\rightarrow$ gửi AJAX tới `/auth/signup` $\rightarrow$ thông báo thành công và chuyển sang đăng nhập.
- **Trang Đăng nhập (`/login`)**: Form nhập Email & Mật khẩu $\rightarrow$ gửi AJAX tới `/auth/login` $\rightarrow$ lưu token vào `localStorage.token` $\rightarrow$ chuyển hướng sang `/user/profile`.
- **Trang Thông tin cá nhân & Quản trị (`/user/profile`)**:
  - Tự động gắn header `Authorization: Bearer <token>` để gọi `/users/me`, hiển thị Avatar, Tên và Email.
  - Tự động gọi API bảo vệ `/users/` để hiển thị bảng danh sách toàn bộ người dùng trong hệ thống.
  - Nút **Đăng xuất (Logout)**: Xóa token trong `localStorage` và điều hướng về trang đăng nhập.

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
│   │   │   │   ├── AuthController.java             # Điều hướng View (/login, /register, /user/profile)
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
│   │       │       └── mainjs.js                   # Xử lý AJAX đăng ký, đăng nhập, gọi API có JWT, logout
│   │       ├── templates/
│   │       │   ├── login.html                      # Giao diện Đăng nhập
│   │       │   ├── register.html                   # Giao diện Đăng ký
│   │       │   └── profile.html                    # Giao diện Thông tin cá nhân & Danh sách user
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

## ⚡ Hướng dẫn chạy và trải nghiệm

### 1. Khởi chạy ứng dụng
```bash
mvn spring-boot:run
```
Ứng dụng khởi động tại: `http://localhost:8005`

### 2. Trải nghiệm trên Web Client (AJAX)
1. Truy cập **Đăng ký tài khoản**: [http://localhost:8005/register](http://localhost:8005/register)
2. Điền thông tin họ tên, email, mật khẩu $\rightarrow$ bấm **Đăng Ký**.
3. Hệ thống chuyển sang **Trang Đăng nhập**: [http://localhost:8005/login](http://localhost:8005/login)
4. Nhập email & mật khẩu vừa tạo $\rightarrow$ bấm **Login**.
5. Đăng nhập thành công, bạn được chuyển đến [http://localhost:8005/user/profile](http://localhost:8005/user/profile):
   - Hiển thị thông tin cá nhân lấy từ JWT Bearer token qua `/users/me`.
   - Hiển thị bảng danh sách toàn bộ người dùng lấy từ `/users/`.
6. Bấm **Đăng xuất (Logout)** để xóa token và kết thúc phiên làm việc.

### 3. Kiểm thử với Postman (REST API)
- **Đăng ký**: `POST http://localhost:8005/auth/signup`
  - Body (raw JSON): `{"fullName": "Nguyen Van A", "email": "a@gmail.com", "password": "123456"}`
- **Đăng nhập**: `POST http://localhost:8005/auth/login`
  - Body (raw JSON): `{"email": "a@gmail.com", "password": "123456"}`
  - Nhận về: `{"token": "...", "expiresIn": 3600000}`
- **Gọi API bảo vệ**: `GET http://localhost:8005/users/me` hoặc `GET http://localhost:8005/users/`
  - Header: `Authorization: Bearer <token_nhan_duoc>`
