package br.com.frotasyncro.controller.home;

import br.com.frotasyncro.application.home.HomeApplicationService;
import br.com.frotasyncro.controller.home.model.HomeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class HomeController {

    private final HomeApplicationService homeApplicationService;

    @GetMapping
    @Operation(summary = "Get home information")
    public ResponseEntity<HomeResponseDTO> homeInfo() {
        HomeResponseDTO homeResponseDTO = homeApplicationService.getHomeInfo();

        return ResponseEntity.ok(homeResponseDTO);
    }

}
