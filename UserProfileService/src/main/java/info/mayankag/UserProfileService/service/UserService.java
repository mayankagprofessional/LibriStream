package info.mayankag.UserProfileService.service;

import billing.BillingResponse;
import info.mayankag.UserProfileService.dto.*;
import info.mayankag.UserProfileService.entity.Role;
import info.mayankag.UserProfileService.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import info.mayankag.UserProfileService.grpc.BillingServiceGrpcClient;
import info.mayankag.UserProfileService.kafka.KafkaProducer;
import info.mayankag.UserProfileService.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Validator validator;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    @Value("${pagination-size}")
    private int paginationSize;

    @Transactional(readOnly = true)
    @Cacheable(value = "${spring.cache.cache-names}", key = "#page")
    public Slice<GetAllUsersResponseDto> getAllUsers(int page) {

        if(page < 1 )
        {
            return new SliceImpl<>(new ArrayList<>());
        }

        Pageable pageable = PageRequest.of(page - 1, paginationSize);
        Slice<User> userSlice = userRepository.findAll(pageable);

        return userSlice.map(user -> GetAllUsersResponseDto.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .age(user.getAge())
                .interests(user.getInterests()
                        .stream()
                        .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                        .toList())
                .build());
    }

    @Transactional
    public Optional<String> registerUser(RegisterRequestDto registerRequestDto) {

        if(registerRequestDto.email() == null || registerRequestDto.email().isEmpty()) {
            return "Email field is missing".describeConstable();
        }

        // Map the DTO to Entity Class
        var user = User.builder()
                .firstname(registerRequestDto.firstName())
                .lastname(registerRequestDto.lastName())
                .email(registerRequestDto.email())
                .age(registerRequestDto.age())
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .role(Role.USER)
                .build();

        // Check if violations occurred for the error
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            // Extract the violation message
            String errorReason = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            // Log the reason for the failure
            log.error("User registration failed for user {}: {}", user.getEmail(),errorReason);

            return errorReason.describeConstable();
        }

        userRepository.save(user);

        // Created User in Billing Service
        BillingResponse billingResponse = billingServiceGrpcClient.createBillingAccount(
                user.getId().toString(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail()
        );

        // Updated Billing Account ID to the current user
        user.setBillingAccountId(billingResponse.getAccountId());
        userRepository.save(user);

        // Notify Users of account creation
        kafkaProducer.notifyUserCreated(user);

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<String> loginUser(LoginRequestDto loginInputDto) {

        // Check credentials and return the JWT Token if credentials match
        return userRepository
                .findByEmail(loginInputDto.email())
                .filter(u -> passwordEncoder.matches(loginInputDto.password(), u.getPassword()))
                .map(u -> jwtService.generateToken(new HashMap<>(), u));
    }

    public boolean validateToken(String token) {
        try {
            jwtService.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
