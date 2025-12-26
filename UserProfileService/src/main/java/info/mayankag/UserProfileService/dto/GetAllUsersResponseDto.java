package info.mayankag.UserProfileService.dto;

import lombok.Builder;
import java.io.Serializable;
import java.util.List;

@Builder
public record GetAllUsersResponseDto(
        String firstname,
        String lastname,
        String email,
        Integer age,
        List<GenreDto> interests
) implements Serializable {

    public GetAllUsersResponseDto {
        interests = (interests == null) ? List.of() : List.copyOf(interests);
    }
}
