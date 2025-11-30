package br.com.frotasyncro.controller.authentication.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private String password;

    public CreateUserRequestDTO(String socialNumber, String email) {
        this.username = socialNumber;
        this.email = email;
    }

}
