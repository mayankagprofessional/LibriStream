package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.CustomAnnotation.StrongPassword;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequestDto(

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s']+$", message = "First name usually contains letters and spaces only")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s']+$", message = "Last name usually contains letters only")
    String lastName,

    @NotNull
    Integer age,

    @NotBlank(message = "Email is required")
    @Email
    String email,

    @NotBlank(message = "Password is required")
    @StrongPassword
    String password
    ) {

}
