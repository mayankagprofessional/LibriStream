package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Slice<GetAllUsersDto>> getAllUsers(
            @RequestParam(defaultValue = "1") int page) {
      return ResponseEntity.ok(userService.getAllUsers(page));
    }

    @PostMapping({"/register"})
    public ResponseEntity<RegisterOutputDto> register(@RequestBody RegisterInputDto registerInputDto) {
        return ResponseEntity.ok(userService.registerUser(registerInputDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutputDto> login(@RequestBody LoginInputDto loginInputDto) {
        return ResponseEntity.ok(userService.loginUser(loginInputDto));
    }
}
