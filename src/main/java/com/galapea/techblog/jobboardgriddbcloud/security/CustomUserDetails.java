package com.galapea.techblog.jobboardgriddbcloud.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.UserDTO;
import com.galapea.techblog.jobboardgriddbcloud.service.UserGridDbService;

@Service
public class CustomUserDetails implements UserDetailsService {

    private final UserGridDbService userGridDbService;

    public CustomUserDetails(final UserGridDbService userGridDbService) {
        this.userGridDbService = userGridDbService;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        Optional<com.galapea.techblog.jobboardgriddbcloud.model.UserDTO> userOpt =
                userGridDbService.getByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else {
            UserDTO userDTO = userOpt.get();
            return org.springframework.security.core.userdetails.User.withUsername(
                            userDTO.getEmail())
                    .password("{noop}123") // for demo purposes only
                    .authorities(userDTO.getRole())
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        }
    }
}
