package info.mayankag.UserProfileService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Genre {
   @Id
   @GeneratedValue
   private Integer id;
   @Column(
      nullable = false
   )
   @NotBlank
   private String name;
}
