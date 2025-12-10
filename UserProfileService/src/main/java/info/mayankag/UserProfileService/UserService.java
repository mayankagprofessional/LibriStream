package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.*;
import info.mayankag.UserProfileService.entity.Role;
import info.mayankag.UserProfileService.entity.User;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Validator validator;

    @Value("${pagination-size}")
    private int paginationSize;

    public Slice<GetAllUsersDto> getAllUsers(int page) {

        Pageable pageable = PageRequest.of(page, paginationSize);
        Slice<User> userSlice = userRepository.findAll(pageable);

        return userSlice.map(user -> GetAllUsersDto.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .age(user.getAge())
                .interests(user.getInterests())
                .build());
    }

    public RegisterOutputDto registerUser(RegisterInputDto registerInputDto) {

        // Map the DTO to Entity Class
        var user = User.builder()
                .firstname(registerInputDto.getFirstName())
                .lastname(registerInputDto.getLastName())
                .email(registerInputDto.getEmail())
                .age(registerInputDto.getAge())
                .password(passwordEncoder.encode(registerInputDto.getPassword()))
                .role(Role.USER)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            // Extract the violation message
            String errorReason = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return RegisterOutputDto
                    .builder()
                    .message("User registration failed! Reason: " + errorReason)
                    .build();
        }

        userRepository.save(user);

        return RegisterOutputDto
                .builder()
                .message("User registered successfully!")
                .build();
    }

    public LoginOutputDto loginUser(LoginInputDto loginInputDto) {

        // Authenticate User
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginInputDto.getEmail(),
                        loginInputDto.getPassword()
                )
        );

        // Check whether user is present or not
        Optional<User> user = userRepository.findByEmail(loginInputDto.getEmail());
        if(user.isPresent()) {
            final String jwtToken = jwtService.generateToken(new HashMap<>(), user.get());
            return LoginOutputDto
                    .builder()
                    .message("Login successful!")
                    .token(jwtToken)
                    .build();
        }

        return LoginOutputDto
                    .builder()
                    .message("Email or Password is incorrect!")
                    .build();
    }
}
