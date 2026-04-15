package com.example.cybersec.auth.service;

import com.example.cybersec.auth.security.MyUserDetails;
import com.example.cybersec.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service xác thực người dùng dựa trên JPA.
 * Spring Security gọi Service này trong quá trình đăng nhập
 * để load thông tin UserDetails từ database.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String identifier = username == null ? "" : username.trim();
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmailIgnoreCase(identifier))
                .map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
