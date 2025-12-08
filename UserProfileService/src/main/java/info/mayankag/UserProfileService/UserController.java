package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.LoginInputDto;
import info.mayankag.UserProfileService.dto.LoginOutputDto;
import info.mayankag.UserProfileService.dto.RegisterInputDto;
import info.mayankag.UserProfileService.dto.RegisterOutputDto;
import info.mayankag.UserProfileService.entity.User;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public List<User> getAllUsers() {
      return userService.getAllUsers();
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
