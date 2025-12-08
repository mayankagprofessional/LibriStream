package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
public class LoginInputDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
