package org.training.food_tracker.dto;

import org.training.food_tracker.model.Lifestyle;
import org.training.food_tracker.model.Role;
import org.training.food_tracker.model.Sex;
import lombok.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString


public class UserDTO {

    private Long id;

    @NotBlank(message = "{userDTO.constraints.username.not_blank}")
//    @Size(min = 2, max = 32, message = "{userDTO.constraints.username.size}")
    private String username;

    @NotBlank(message = "{userDTO.constraints.password.not_blank}")
//    @Size(min = 4, max = 32, message = "{userDTO.constraints.password.size}")
    private String password;

    @NotBlank(message = "{userDTO.constraints.full_name.not_blank}")
//    @Size(min = 2, max = 32, message = "{userDTO.constraints.full_name.size}")
    private String fullName;

    @NotBlank(message = "{userDTO.constraints.national_name.not_blank}")
//    @Size(min = 2, max = 32, message = "{userDTO.constraints.national_name.size}")
    private String nationalName;

    @NotBlank(message = "{userDTO.constraints.email.not_blank}")
//    @Email(message = "{userDTO.constraints.email.size}")
    private String email;

    private Role role;

    @NotNull(message = "{userDTO.constraints.age.not_blank}")
    @Min(value = 18, message = "{userDTO.constraints.age.min}")
    @Max(value = 200, message = "{userDTO.constraints.age.max}")
    private BigDecimal age;

    private Sex sex;

    @NotNull(message = "{userDTO.constraints.weight.not_blank}")
    @Min(value = 1, message = "{userDTO.constraints.weight.min}")
    @Max(value = 500, message = "{userDTO.constraints.weight.max}")
    private BigDecimal weight;

    @NotNull(message = "{userDTO.constraints.height.not_blank}")
    @Min(value = 1, message = "{userDTO.constraints.height.min}")
    @Max(value = 300, message = "{userDTO.constraints.height.max}")
    private BigDecimal height;
    private Lifestyle lifestyle;
    private BigDecimal dailyNorm;

}
