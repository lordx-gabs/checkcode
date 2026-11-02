package com.edu.checkcode.service;

import com.edu.checkcode.dto.LoginResponse;
import com.edu.checkcode.dto.RegisterEmployeeRequest;
import com.edu.checkcode.dto.RegisterUserRequest;
import com.edu.checkcode.dto.UpdateUserRequest;
import com.edu.checkcode.dto.UserResponse;
import com.edu.checkcode.exception.EmailAlreadyRegisteredException;
import com.edu.checkcode.exception.UnauthorizedException;
import com.edu.checkcode.model.Role;
import com.edu.checkcode.model.User;
import com.edu.checkcode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setBirthDate(request.birthDate());
        user.setGender(request.gender());
        user.setRole(Role.USER);

        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse registerEmployee(RegisterEmployeeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Authentication not found");
        }
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (role == null) {
            throw new UnauthorizedException("Role not found");
        }

        if (!Role.MANAGER.name().equals(role)) {
            throw new UnauthorizedException("Only MANAGER role can register employees");
        }

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User employee = new User();
        employee.setName(request.name().trim());
        employee.setEmail(email);
        employee.setPasswordHash(passwordEncoder.encode(request.password()));
        employee.setBirthDate(request.birthDate());
        employee.setGender(request.gender());
        employee.setRole(Role.EMPLOYEE);

        User savedEmployee = userRepository.save(employee);
        String employeeToken = jwtService.generateToken(savedEmployee);

        return new LoginResponse(
                employeeToken,
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail(),
                savedEmployee.getRole()
        );
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if(authentication == null) {
            throw new UnauthorizedException("Authentication not found");
        }

        String currentUserId = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUserId.equals(userId.toString()) && !Role.MANAGER.name().equals(
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null))) {
            throw new UnauthorizedException("You can only update your own data or be a MANAGER");
        }

        if (request.name() != null) {
            user.setName(request.name().trim());
        }

        if (request.email() != null) {
            String email = request.email().trim().toLowerCase(Locale.ROOT);
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new EmailAlreadyRegisteredException(email);
            }
            user.setEmail(email);
        }

        if (request.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }

        if (request.gender() != null) {
            user.setGender(request.gender());
        }

        return UserResponse.from(userRepository.save(user));
    }
}
