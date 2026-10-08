package com.mediastream.service;

import com.mediastream.config.JwtTokenProvider;
import com.mediastream.dto.AuthRequest;
import com.mediastream.dto.AuthResponse;
import com.mediastream.dto.RegisterRequest;
import com.mediastream.dto.UserDto;
import com.mediastream.entity.Role;
import com.mediastream.entity.User;
import com.mediastream.exception.ApiException;
import com.mediastream.repository.RoleRepository;
import com.mediastream.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ApiException("Email is already registered!", HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                req.getUsername(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword())
        );

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);

        // Authenticate & produce token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        List<String> roles = savedUser.getRoles().stream().map(Role::getName).toList();
        List<String> permissions = savedUser.getActivePermissionNames();

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), roles, permissions);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest req) {
        // Find by username or email first to resolve the principal username
        User user = userRepository.findByUsernameOrEmail(req.getUsername(), req.getUsername())
                .orElseThrow(() -> new ApiException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        List<String> permissions = user.getActivePermissionNames();

        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles, permissions);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRoles(user.getRoles().stream().map(Role::getName).toList());

        List<UserDto.ActivePermissionDto> activePerms = user.getPermissionGrants().stream()
                .filter(grant -> grant.isActive())
                .map(grant -> new UserDto.ActivePermissionDto(
                        grant.getPermission().getId(),
                        grant.getPermission().getName(),
                        grant.getPermission().getDescription(),
                        grant.getExpiresAt(),
                        grant.getExpiresAt() != null,
                        grant.getGrantType()
                ))
                .toList();

        dto.setActivePermissions(activePerms);
        return dto;
    }
}
