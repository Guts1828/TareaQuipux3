package co.edu.uco.academico.service;

import co.edu.uco.academico.dto.Dtos.LoginRequest;
import co.edu.uco.academico.dto.Dtos.LoginResponse;
import co.edu.uco.academico.exception.ApiException;
import co.edu.uco.academico.model.Usuario;
import co.edu.uco.academico.repository.UsuarioRepository;
import co.edu.uco.academico.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarios.findByEmail(req.email())
                .filter(Usuario::isActivo)
                .filter(x -> passwordEncoder.matches(req.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return new LoginResponse(jwtService.generar(u.getEmail(), u.getRol()), u.getRol().name(), u.getEmail());
    }
}
