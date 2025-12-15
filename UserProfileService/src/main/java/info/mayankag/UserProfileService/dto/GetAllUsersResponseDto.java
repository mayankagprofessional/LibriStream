package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.entity.Genre;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetAllUsersResponseDto {

    private String firstname;
    private String lastname;
    private String email;
    private Integer age;
    private List<Genre> interests;
}
