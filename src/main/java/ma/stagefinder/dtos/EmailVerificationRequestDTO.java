package ma.stagefinder.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequestDTO  {

    @NotBlank(message = "Token is required")
    private String token;
}
