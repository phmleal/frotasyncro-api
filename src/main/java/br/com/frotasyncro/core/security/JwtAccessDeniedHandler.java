package br.com.frotasyncro.core.security;

import br.com.frotasyncro.core.exception.model.ApiExceptionErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ApiExceptionErrorDTO apiExceptionErrorDTO =
                new ApiExceptionErrorDTO(
                        accessDeniedException.getClass().getSimpleName(),
                        "You do not have permission to access this resource"
                );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(apiExceptionErrorDTO.toJson());
    }

}

