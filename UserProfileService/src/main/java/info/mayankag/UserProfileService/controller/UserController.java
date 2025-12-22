package info.mayankag.UserProfileService.controller;

import info.mayankag.UserProfileService.service.CacheInspectionService;
import info.mayankag.UserProfileService.service.UserService;
import info.mayankag.UserProfileService.dto.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping({"/api/user"})
@RequiredArgsConstructor
@Tag(name = "User", description = "API for managing Users")
public class UserController {

    private final UserService userService;
    private final CacheInspectionService cacheInspectionService;

    @GetMapping
    @Operation(summary = "Get Users", description = "Return users with Pagination")
    public ResponseEntity<Slice<GetAllUsersResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "1") int page) {
      return ResponseEntity.ok(userService.getAllUsers(page));
    }

    @PostMapping({"/register"})
    @Operation(summary = "Register User", description = "Register New User")
    public ResponseEntity<RegisterResponseDto> register(
            @RequestBody @Valid RegisterRequestDto registerRequestDto) {

        // Register User and return the boolean status
        Optional<String> errorMessage = userService.registerUser(registerRequestDto);

        // Return response based on the status received
        return errorMessage.map(
                s -> ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponseDto(s))).
                orElseGet(() -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new RegisterResponseDto("User registered successfully!")));
    }

    @PostMapping("/login")
    @Operation(summary = "Login User", description = "Login Existing User")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginInputDto) {

        // Check credentials and return JWT Token if present
        Optional<String> token = userService.loginUser(loginInputDto);

        // Return response based on the token received
        return token.map(
                s -> ResponseEntity.ok(new LoginResponseDto(s, "Login successful!")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponseDto(null,
                                "Username or Password is incorrect!")));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT Token")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.validateToken(authHeader.substring(7))
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/readCache")
    public ResponseEntity<Void> readCacheData(@RequestParam String cacheName) {
        cacheInspectionService.printCacheContents(cacheName);
        return ResponseEntity.noContent().build();
    }
}
