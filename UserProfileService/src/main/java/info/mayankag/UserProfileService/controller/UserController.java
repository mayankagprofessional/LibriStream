package info.mayankag.UserProfileService.controller;

import info.mayankag.UserProfileService.service.UserService;
import info.mayankag.UserProfileService.dto.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/user"})
@RequiredArgsConstructor
@Tag(name = "User", description = "API for managing Users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get Users", description = "Return users with Pagination")
    public ResponseEntity<Slice<GetAllUsersDto>> getAllUsers(
            @RequestParam(defaultValue = "1") int page) {
      return ResponseEntity.ok(userService.getAllUsers(page));
    }

    @PostMapping({"/register"})
    @Operation(summary = "Register User", description = "Register New User")
    public ResponseEntity<RegisterOutputDto> register(@RequestBody RegisterInputDto registerInputDto) {
        return ResponseEntity.ok(userService.registerUser(registerInputDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login User", description = "Login Existing User")
    public ResponseEntity<LoginOutputDto> login(@RequestBody LoginInputDto loginInputDto) {
        return ResponseEntity.ok(userService.loginUser(loginInputDto));
    }
}
