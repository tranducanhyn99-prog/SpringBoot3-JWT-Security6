package vn.iotstar.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .fullName("Quản trị viên")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .images("default-avatar.png")
                        .build();
                userRepository.save(admin);
            }

            if (userRepository.findByEmail("trungnh@hcmute.edu.vn").isEmpty()) {
                User teacher = User.builder()
                        .fullName("ThS. Nguyễn Hữu Trung")
                        .email("trungnh@hcmute.edu.vn")
                        .password(passwordEncoder.encode("123456"))
                        .images("default-avatar.png")
                        .build();
                userRepository.save(teacher);
            }
        };
    }
}
