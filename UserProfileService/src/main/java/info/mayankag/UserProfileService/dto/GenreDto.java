package info.mayankag.UserProfileService.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record GenreDto(
        Integer id,
        String name) implements Serializable {
}