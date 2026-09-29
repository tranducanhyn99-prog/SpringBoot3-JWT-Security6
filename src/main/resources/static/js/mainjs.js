$(document).ready(function() {
    // Hiển thị thông tin người dùng đăng nhập thành công trên trang profile
    if ($('#profile').length) {
        if (!localStorage.token) {
            alert("Sorry, you are not logged in.");
            window.location.href = "/login";
            return;
        }

        $.ajax({
            type: 'GET',
            url: '/users/me',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            beforeSend: function(xhr) {
                if (localStorage.token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                }
            },
            success: function(data) {
                console.log("SUCCESS: ", data);
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
            error: function(e) {
                console.log("ERROR: ", e);
                alert("Sorry, you are not logged in or your session has expired.");
                localStorage.clear();
                window.location.href = "/login";
            }
        });
    }

    // Hàm đăng xuất
    $('#logout').click(function() {
        localStorage.clear();
        window.location.href = "/login";
    });

    // Hàm login
    $('#login').click(function() {
        var email = document.getElementById('email').value;
        var password = document.getElementById('password').value;

        if (!email || !password) {
            alert("Vui lòng nhập đầy đủ Email và Mật khẩu!");
            return;
        }

        var basicInfo = JSON.stringify({
            email: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/login",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: basicInfo,
            success: function(data) {
                localStorage.token = data.token;
                console.log('Got a token from the server! Token: ' + data.token);
                window.location.href = "/user/profile";
            },
            error: function(e) {
                console.log("Login Failed: ", e);
                var errorMsg = "Login Failed! Vui lòng kiểm tra lại email hoặc mật khẩu.";
                if (e.responseJSON && e.responseJSON.description) {
                    errorMsg = e.responseJSON.description;
                }
                alert(errorMsg);
            }
        });
    });
});
