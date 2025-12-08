package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.CustomAnnotation.StrongPassword;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterInputDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s']+$", message = "Name usually contains letters and spaces only")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s']+$", message = "Last Name usually contains letters only")
    private String lastName;

    @NotNull
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    @StrongPassword
    private String password;
}
