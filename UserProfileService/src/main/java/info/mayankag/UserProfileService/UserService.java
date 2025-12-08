package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.RegisterInputDto;
import info.mayankag.UserProfileService.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
   private final UserRepository userRepository;

   public UserService(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   public List<User> getAllUsers() {
      return userRepository.findAll();
   }

   public boolean registerUser(RegisterInputDto registerInputDto) {
      return true;
   }
}
