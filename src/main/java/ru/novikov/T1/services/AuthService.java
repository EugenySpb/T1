package ru.novikov.T1.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.novikov.T1.dto.JwtResponse;
import ru.novikov.T1.dto.LoginRequest;
import ru.novikov.T1.dto.MessageResponse;
import ru.novikov.T1.dto.SignupRequest;
import ru.novikov.T1.models.Role;
import ru.novikov.T1.models.RoleEnum;
import ru.novikov.T1.models.User;
import ru.novikov.T1.repositories.RoleRepository;
import ru.novikov.T1.repositories.UserRepository;
import ru.novikov.T1.services.impl.UserDetailsImpl;
import ru.novikov.T1.util.JwtUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    public MessageResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByLogin(signupRequest.getUsername())) {
            return new MessageResponse("Error: Username already exists!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new MessageResponse("Error: Email already exists!");
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        Set<Role> roles = getRolesFromRequest(signupRequest.getRole());
        user.setRoles(roles);

        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    private Set<Role> getRolesFromRequest(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByRole(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found!"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(
                            roleRepository.findByRole(RoleEnum.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role not found!"))
                    );
                    case "moderator" -> roles.add(
                            roleRepository.findByRole(RoleEnum.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role not found!"))
                    );
                    default -> roles.add(
                            roleRepository.findByRole(RoleEnum.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role not found!"))
                    );
                }
            });
        }
        return roles;
    }
}
