package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.CustomAnnotation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(
        @NotBlank(message = "Email is required")
        @Email
        String email,

        @NotBlank(message = "Password is required")
        @StrongPassword
        String password
) {

}
