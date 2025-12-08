package info.mayankag.UserProfileService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginOutputDto {

    private String token;

    @NotBlank
    private String message;
}
