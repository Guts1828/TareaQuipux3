package co.edu.uco.academico.controller;

import co.edu.uco.academico.dto.Dtos.*;
import co.edu.uco.academico.service.ProfesorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesor")
@RequiredArgsConstructor
@Tag(name = "Profesor", description = "Registro de estudiantes, cursos, matrículas y calificaciones")
@SecurityRequirement(name = "bearerAuth")
public class ProfesorController {

    private final ProfesorService service;

    // ---------- Estudiantes ----------
    @PostMapping("/estudiantes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un estudiante (crea también su usuario de acceso)")
    public EstudianteResponse registrarEstudiante(@Valid @RequestBody EstudianteRequest req) {
        return service.registrarEstudiante(req);
    }

    @GetMapping("/estudiantes")
    @Operation(summary = "Listar todos los estudiantes")
    public List<EstudianteResponse> listarEstudiantes() {
        return service.listarEstudiantes();
    }

    // ---------- Cursos ----------
    @PostMapping("/cursos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un curso a cargo del profesor autenticado")
    public CursoResponse crearCurso(Authentication auth, @Valid @RequestBody CursoRequest req) {
        return service.crearCurso(auth.getName(), req);
    }

    @GetMapping("/cursos")
    @Operation(summary = "Listar mis cursos")
    public List<CursoResponse> misCursos(Authentication auth) {
        return service.misCursos(auth.getName());
    }

    @GetMapping("/cursos/{cursoId}/estudiantes")
    @Operation(summary = "Ver estudiantes de un curso con sus notas y definitiva")
    public List<EstudianteCursoResponse> estudiantesDeCurso(Authentication auth, @PathVariable Long cursoId) {
        return service.estudiantesDeCurso(auth.getName(), cursoId);
    }

    // ---------- Matrículas ----------
    @PostMapping("/matriculas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Matricular un estudiante en uno de mis cursos")
    public MatriculaResponse matricular(Authentication auth, @Valid @RequestBody MatriculaRequest req) {
        return service.matricular(auth.getName(), req);
    }

    // ---------- Calificaciones ----------
    @PostMapping("/calificaciones")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una nota (escala 0.0 a 5.0, porcentajes suman máx. 100)")
    public CalificacionResponse calificar(Authentication auth, @Valid @RequestBody CalificacionRequest req) {
        return service.calificar(auth.getName(), req);
    }

    @PutMapping("/calificaciones/{id}")
    @Operation(summary = "Modificar una nota")
    public CalificacionResponse actualizar(Authentication auth, @PathVariable Long id,
                                           @Valid @RequestBody CalificacionRequest req) {
        return service.actualizarCalificacion(auth.getName(), id, req);
    }

    @DeleteMapping("/calificaciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una nota")
    public void eliminar(Authentication auth, @PathVariable Long id) {
        service.eliminarCalificacion(auth.getName(), id);
    }
}
