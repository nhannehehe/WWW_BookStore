//package com.ecom.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.ecom.model.UserDtls;
//import com.ecom.repository.UserRepository;
//
//@Component
//public class DatabaseInitializer implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Check if admin user already exists
//        if (userRepository.findByEmail("admin@admin.com") == null) {
//            UserDtls adminUser = new UserDtls();
//            adminUser.setEmail("admin@admin.com");
//            // Ensure password is properly encoded
//            String encodedPassword = passwordEncoder.encode("123");
//            adminUser.setPassword(encodedPassword);
//            adminUser.setRole("ROLE_ADMIN");
//            adminUser.setName("Administrator");
//            adminUser.setMobileNumber("0000000000");
//            adminUser.setAddress("Admin Address");
//            adminUser.setCity("Admin City");
//            adminUser.setState("Admin State");
//            adminUser.setPincode("000000");
//            adminUser.setIsEnable(true);
//            adminUser.setAccountNonLocked(true);
//            adminUser.setFailedAttempt(0);
//            adminUser.setProfileImage("default.jpg");
//
//            UserDtls savedUser = userRepository.save(adminUser);
//            System.out.println("Admin user created successfully with ID: " + savedUser.getId());
//            System.out.println("Encoded password: " + encodedPassword);
//        } else {
//            System.out.println("Admin user already exists");
//        }
//    }
//}