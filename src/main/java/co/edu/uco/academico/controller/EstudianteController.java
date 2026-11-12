package co.edu.uco.academico.controller;

import co.edu.uco.academico.dto.Dtos.NotaCurso;
import co.edu.uco.academico.dto.Dtos.PanelEstudiante;
import co.edu.uco.academico.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiante")
@RequiredArgsConstructor
@Tag(name = "Panel del Estudiante", description = "Cada estudiante solo ve su propia información")
@SecurityRequirement(name = "bearerAuth")
public class EstudianteController {

    private final EstudianteService service;

    @GetMapping("/panel")
    @Operation(summary = "Mi panel: perfil, cursos, notas, definitivas y promedio general")
    public PanelEstudiante panel(Authentication auth) {
        return service.panel(auth.getName());
    }

    @GetMapping("/cursos")
    @Operation(summary = "Mis cursos con sus calificaciones")
    public List<NotaCurso> misCursos(Authentication auth) {
        return service.misCursos(auth.getName());
    }

    @GetMapping("/cursos/{cursoId}/calificaciones")
    @Operation(summary = "Mis calificaciones de un curso")
    public NotaCurso notasDeCurso(Authentication auth, @PathVariable Long cursoId) {
        return service.notasDeCurso(auth.getName(), cursoId);
    }
}
