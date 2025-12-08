package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.RegisterInputDto;
import info.mayankag.UserProfileService.entity.User;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Generated;
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

   @GetMapping
   public List<User> getAllUsers() {
      return userService.getAllUsers();
   }

   @PostMapping({"/register"})
   public void register(@RequestBody RegisterInputDto registerInputDto) {
      userService.registerUser(registerInputDto);
   }

   public void login(@RequestBody RegisterInputDto registerInputDto) {

   }


}
