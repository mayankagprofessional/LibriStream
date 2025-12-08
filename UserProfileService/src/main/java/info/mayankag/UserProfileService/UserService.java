package info.mayankag.UserProfileService;

import info.mayankag.UserProfileService.dto.LoginInputDto;
import info.mayankag.UserProfileService.dto.LoginOutputDto;
import info.mayankag.UserProfileService.dto.RegisterInputDto;
import info.mayankag.UserProfileService.dto.RegisterOutputDto;
import info.mayankag.UserProfileService.entity.Role;
import info.mayankag.UserProfileService.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public RegisterOutputDto registerUser(RegisterInputDto registerInputDto) {

        // Map the DTO to Entity Class
        User user = User.builder()
                .firstname(registerInputDto.getFirstName())
                .lastname(registerInputDto.getLastName())
                .email(registerInputDto.getEmail())
                .age(registerInputDto.getAge())
                .password(passwordEncoder.encode(registerInputDto.getPassword()))
                .role(Role.USER)
                .interests(new ArrayList<>())
                .build();
        userRepository.save(user);

        return RegisterOutputDto
                .builder()
                .message("User registered successfully!")
                .build();
    }

    public LoginOutputDto loginUser(LoginInputDto loginInputDto) {

        // Check whether user is present or not
        Optional<User> user = userRepository.findByEmail(loginInputDto.getEmail());
        if(user.isPresent()) {
            final String jwtToken = jwtService.generateToken(new HashMap<>(), user.get());
            return LoginOutputDto
                    .builder()
                    .message("Login successfully!")
                    .token(jwtToken)
                    .build();
        }

        return LoginOutputDto
                    .builder()
                    .message("Email or Password is incorrect!")
                    .build();
    }
}
