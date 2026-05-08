package id.ac.ui.cs.advprog.mysawit.config; // Sesuaikan dengan struktur folder kamu

import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Mengecek apakah username "admin" sudah ada di database
            if (userRepository.findByUsername("admin").isEmpty()) {

                // Kita gunakan setter agar terhindar dari error mismatch constructor seperti di test sebelumnya
                User admin = new User();
                admin.setName("Admin Utama MySawit");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Password akan di-enkripsi
                admin.setRole(Role.ADMIN); // Menggunakan Role ADMIN

                userRepository.save(admin);

                System.out.println("==========================================");
                System.out.println("✅ AKUN ADMIN OTOMATIS BERHASIL DIBUAT!");
                System.out.println("✅ Username : admin");
                System.out.println("✅ Password : admin123");
                System.out.println("==========================================");
            }
        };
    }
}