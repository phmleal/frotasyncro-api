package br.com.frotasyncro.core.exception.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiExceptionErrorDTO {

    private String error;
    private String detail;
    private Boolean credentialsExpired;
    private Boolean disabledUser;

    public ApiExceptionErrorDTO(String error, String detail) {
        this.error = error;
        this.detail = detail;
    }

    public String toJson() {
        return "{" + "\"error\": \"" + getError() + "\", \"detail\": \"" + getDetail() + "\" }";
    }

}
