package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.RegisterInputDto;
import info.mayankag.UserProfileService.entity.User;
import java.util.List;
import lombok.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/user"})
public class UserController {
   private final UserService userService;

   @GetMapping
   public List<User> getAllUsers() {
      return this.userService.getAllUsers();
   }

   @PostMapping({"/register"})
   public void register(@RequestBody RegisterInputDto registerInputDto) {
      this.userService.registerUser(registerInputDto);
   }

   @Generated
   public UserController(final UserService userService) {
      this.userService = userService;
   }
}
