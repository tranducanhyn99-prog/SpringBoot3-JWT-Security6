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

#### Chạy với H2 Database (Mặc định, không cần cài CSDL)
Dự án được cấu hình mặc định sử dụng H2 Database in-memory (`spring.profiles.active=h2`). Mở terminal tại thư mục dự án và chạy:
```bash
mvn spring-boot:run
```
- **Server chạy tại**: `http://localhost:8005`
- **H2 Console**: `http://localhost:8005/h2-console` (JDBC URL: `jdbc:h2:mem:jwt_db`, User: `sa`, Password: để trống)

#### Chuyển sang Microsoft SQL Server
Trong file `src/main/resources/application.properties`, đổi:
```properties
spring.profiles.active=sqlserver
```
Và kiểm tra thông số kết nối trong `src/main/resources/application-sqlserver.properties`.

---

### 🔑 2. Tài khoản mẫu có sẵn (Khởi tạo tự động)
Ứng dụng tích hợp sẵn `DataInitializer` tự động nạp 2 tài khoản test vào CSDL ngay khi khởi động:

| Họ và tên | Email (Username) | Mật khẩu | Vai trò |
| :--- | :--- | :---: | :--- |
| **Quản trị viên** | `admin@gmail.com` | `123456` | Quản trị hệ thống |
| **ThS. Nguyễn Hữu Trung** | `trungnh@hcmute.edu.vn` | `123456` | Giảng viên (chuẩn theo slide bài giảng) |

> 💡 **Mẹo**: Tại trang đăng nhập (`/login`), có sẵn **hộp gợi ý tài khoản mẫu kèm nút "Điền"**. Bạn chỉ cần click để tự động điền nhanh mà không cần nhập tay.

---

### 🌐 3. Trải nghiệm trên Giao diện Web Client (AJAX)

#### Bước 1: Đăng nhập
1. Truy cập: [http://localhost:8005/login](http://localhost:8005/login)
2. Bấm nút **"Điền"** cạnh tài khoản mẫu hoặc nhập Email & Password $\rightarrow$ Bấm **Login**.
3. **Cơ chế hoạt động**:
   - AJAX gửi request `POST /auth/login` lên server.
   - Nhận chuỗi **Nimbus JWT Token** và lưu vào `localStorage.token`.
   - Trình duyệt tự động chuyển hướng đến trang `/user/profile`.

#### Bước 2: Xem thông tin cá nhân & Danh sách người dùng
- Tại trang [http://localhost:8005/user/profile](http://localhost:8005/user/profile):
  - AJAX tự động đọc token từ `localStorage`, đính kèm header `Authorization: Bearer <token>` gọi API `/users/me`.
  - Hiển thị Avatar, Họ tên và Email người dùng.
  - Tự động gọi API `/users/` kèm Bearer token để load và hiển thị bảng danh sách tất cả tài khoản trong hệ thống.

#### Bước 3: Đăng ký tài khoản mới
1. Truy cập [http://localhost:8005/register](http://localhost:8005/register) (hoặc bấm *"Đăng ký ngay"* ở trang login).
2. Điền Họ tên, Email, Mật khẩu $\rightarrow$ Bấm **Đăng Ký**.
3. Hệ thống gửi AJAX đến `/auth/signup`, thông báo thành công và chuyển về trang đăng nhập.
4. Đăng nhập bằng tài khoản mới vừa tạo $\rightarrow$ Bạn sẽ thấy tài khoản xuất hiện ngay trong bảng danh sách user.

#### Bước 4: Đăng xuất & Kiểm tra bảo mật
1. Bấm nút **"Đăng xuất (Logout)"** $\rightarrow$ Token trong `localStorage` bị xóa sạch.
2. Thử truy cập trực tiếp lại [http://localhost:8005/user/profile](http://localhost:8005/user/profile) $\rightarrow$ Hệ thống cảnh báo chưa đăng nhập và điều hướng ngay về `/login`.

---

### 🧪 4. Kiểm thử với Postman (REST API Chi tiết)

#### a. Đăng nhập lấy Token (`POST /auth/login`)
- **URL**: `http://localhost:8005/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "email": "trungnh@hcmute.edu.vn",
    "password": "123456"
  }
  ```
- **Response** (HTTP 200 OK):
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0cnVuZ25oQGhjbXV0ZS5lZHUudm4iLCJpc3MiOiJ2bi5pb3RzdGFyIiw...",
    "expiresIn": 3600000
  }
  ```
  *(Copy chuỗi token này để sử dụng cho các request sau)*.

#### b. Truy cập Endpoint bảo vệ (`GET /users/me` & `GET /users/`)
- **URL**: `http://localhost:8005/users/me` hoặc `http://localhost:8005/users/`
- **Headers**:
  - `Authorization`: `Bearer <chuỗi_token_vừa_copy>`
- **Response** (HTTP 200 OK): Trả về thông tin cá nhân trích xuất từ Token context.

#### c. Đăng ký tài khoản mới (`POST /auth/signup`)
- **URL**: `http://localhost:8005/auth/signup`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "fullName": "Trần Đức Anh",
    "email": "ducanh@gmail.com",
    "password": "mypassword123"
  }
  ```

#### d. Kiểm thử các trường hợp ngoại lệ bảo mật (GlobalExceptionHandler)
- **Không gửi Token**: Gọi `GET /users/me` không có header Authorization $\rightarrow$ Nhận mã lỗi **`403 Forbidden`**.
- **Token sai / chữ ký giả mạo**: Sửa 1 ký tự bất kỳ trong chuỗi token $\rightarrow$ Nhận mã lỗi **`401 Unauthorized`** kèm RFC 7807 ProblemDetail:
  ```json
  {
    "status": 401,
    "detail": "Invalid JWT signature",
    "description": "The JWT signature is invalid"
  }
  ```
- **Sai mật khẩu**: Gọi `POST /auth/login` với mật khẩu sai $\rightarrow$ Nhận mã lỗi **`401 Unauthorized`**:
  ```json
  {
    "status": 401,
    "detail": "Bad credentials",
    "description": "The username or password is incorrect"
  }
  ```

---

### ⚡ 5. Chạy Kiểm thử tự động (Automated Test)
Chạy bộ test tích hợp toàn diện trong 5 giây mà không cần mở trình duyệt:
```bash
mvn test
```
Toàn bộ kịch bản đăng ký $\rightarrow$ đăng nhập sinh Nimbus JWT $\rightarrow$ gọi API bảo vệ $\rightarrow$ chặn request không token sẽ được thực thi tự động đạt kết quả **BUILD SUCCESS**.
