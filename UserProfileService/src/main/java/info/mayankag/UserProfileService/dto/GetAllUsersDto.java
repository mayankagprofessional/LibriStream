package info.mayankag.UserProfileService.dto;

import info.mayankag.UserProfileService.entity.Genre;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetAllUsersDto {

    private String firstname;
    private String lastname;
    private String email;
    private Integer age;
    private List<Genre> interests;
}
