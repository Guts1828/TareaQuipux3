package co.edu.uco.academico.controller;

import co.edu.uco.academico.dto.Dtos.LoginRequest;
import co.edu.uco.academico.dto.Dtos.LoginResponse;
import co.edu.uco.academico.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión (devuelve un token JWT)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Devuelve el token JWT. Úsalo con el botón 'Authorize' de Swagger.")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
