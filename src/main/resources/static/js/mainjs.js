$(document).ready(function() {

    // 1. Xử lý ĐĂNG KÝ TÀI KHOẢN (register.html)
    $('#btnRegister').click(function() {
        var fullName = $('#fullName').val().trim();
        var email = $('#email').val().trim();
        var password = $('#password').val();
        var confirmPassword = $('#confirmPassword').val();

        if (!fullName || !email || !password || !confirmPassword) {
            alert("Vui lòng điền đầy đủ các thông tin!");
            return;
        }

        if (password.length < 6) {
            alert("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (password !== confirmPassword) {
            alert("Mật khẩu xác nhận không khớp!");
            return;
        }

        var registerData = JSON.stringify({
            fullName: fullName,
            email: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/signup",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: registerData,
            success: function(response) {
                alert("Đăng ký tài khoản thành công! Đang chuyển đến trang đăng nhập...");
                window.location.href = "/login";
            },
            error: function(xhr) {
                console.error("Signup Failed: ", xhr);
                var message = "Đăng ký thất bại. Vui lòng thử lại!";
                if (xhr.responseJSON && xhr.responseJSON.detail) {
                    message = xhr.responseJSON.detail;
                } else if (xhr.responseJSON && xhr.responseJSON.description) {
                    message = xhr.responseJSON.description;
                }
                alert(message);
            }
        });
    });

    // 2. Xử lý ĐĂNG NHẬP (login.html)
    $('#login').click(function() {
        var email = $('#email').val().trim();
        var password = $('#password').val();

        if (!email || !password) {
            alert("Vui lòng nhập đầy đủ Email và Mật khẩu!");
            return;
        }

        var loginData = JSON.stringify({
            email: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/login",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: loginData,
            success: function(data) {
                localStorage.token = data.token;
                console.log('Login success! JWT Token:', data.token);
                window.location.href = "/user/profile";
            },
            error: function(xhr) {
                console.error("Login Failed: ", xhr);
                var errorMsg = "Đăng nhập thất bại! Email hoặc mật khẩu không chính xác.";
                if (xhr.responseJSON && xhr.responseJSON.description) {
                    errorMsg = xhr.responseJSON.description;
                }
                alert(errorMsg);
            }
        });
    });

    // 3. Xử lý hiển thị thông tin trang PROFILE (profile.html)
    if ($('#profile').length) {
        if (!localStorage.token) {
            alert("Bạn chưa đăng nhập hoặc phiên đã kết thúc. Vui lòng đăng nhập!");
            window.location.href = "/login";
            return;
        }

        // Lấy thông tin user hiện tại (/users/me)
        $.ajax({
            type: 'GET',
            url: '/users/me',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
            },
            success: function(data) {
                console.log("Current User: ", data);
                $('#profile').html(data.fullName || data.username || data.email);
                if (data.email) {
                    $('#userEmail').html(data.email);
                }
                if (data.images) {
                    var imgSrc = data.images.startsWith("http") || data.images.startsWith("/") 
                        ? data.images 
                        : "/images/" + data.images;
                    $('#images').attr("src", imgSrc);
                }
            },
            error: function(xhr) {
                console.error("Lỗi lấy thông tin user: ", xhr);
                alert("Phiên đăng nhập không hợp lệ hoặc đã hết hạn!");
                localStorage.clear();
                window.location.href = "/login";
            }
        });

        // Hàm tải danh sách tất cả người dùng (/users/)
        function loadAllUsers() {
            $.ajax({
                type: 'GET',
                url: '/users/',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                },
                success: function(users) {
                    var tbody = $('#usersTableBody');
                    tbody.empty();

                    if (!users || users.length === 0) {
                        tbody.append('<tr><td colspan="4" class="text-center text-muted">Không có người dùng nào.</td></tr>');
                        return;
                    }

                    users.forEach(function(u) {
                        var dateStr = u.createdAt ? new Date(u.createdAt).toLocaleString('vi-VN') : 'N/A';
                        var row = '<tr>' +
                            '<td>' + (u.id || '') + '</td>' +
                            '<td><strong>' + (u.fullName || '') + '</strong></td>' +
                            '<td>' + (u.email || '') + '</td>' +
                            '<td>' + dateStr + '</td>' +
                            '</tr>';
                        tbody.append(row);
                    });
                },
                error: function(xhr) {
                    console.error("Lỗi lấy danh sách user: ", xhr);
                }
            });
        }

        // Tải danh sách user lần đầu và khi bấm nút Reload
        loadAllUsers();
        $('#btnReloadUsers').click(loadAllUsers);
    }

    // 4. Xử lý ĐĂNG XUẤT (profile.html)
    $('#logout').click(function() {
        localStorage.clear();
        alert("Đã đăng xuất thành công!");
        window.location.href = "/login";
    });
});
