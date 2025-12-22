package info.mayankag.UserProfileService.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersResponseDto implements Serializable {

    private String firstname;
    private String lastname;
    private String email;
    private Integer age;
    private List<GenreDto> interests;
}
