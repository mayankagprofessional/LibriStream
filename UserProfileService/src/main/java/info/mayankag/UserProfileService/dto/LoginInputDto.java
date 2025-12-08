package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.CustomAnnotation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
public class LoginInputDto {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    @StrongPassword
    private String password;
}
