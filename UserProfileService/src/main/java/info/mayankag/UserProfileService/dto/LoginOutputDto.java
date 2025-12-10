package info.mayankag.UserProfileService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginOutputDto {

    private String token;
    private String message;
}
