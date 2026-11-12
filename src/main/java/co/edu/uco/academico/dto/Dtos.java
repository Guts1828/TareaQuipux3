package co.edu.uco.academico.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Todos los DTOs de la API agrupados como records. */
public final class Dtos {

    private Dtos() {}

    // ---------- Auth ----------
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record LoginResponse(String token, String rol, String email) {}

    // ---------- Estudiantes ----------
    public record EstudianteRequest(
            @NotBlank @Size(max = 20) String codigo,
            @NotBlank String nombres,
            @NotBlank String apellidos,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password,
            @NotBlank String programa,
            @NotNull @Min(1) @Max(12) Integer semestre) {}

    public record EstudianteResponse(
            Long id, String codigo, String nombres, String apellidos,
            String email, String programa, Integer semestre) {}

    // ---------- Cursos ----------
    public record CursoRequest(
            @NotBlank @Size(max = 20) String codigo,
            @NotBlank String nombre,
            @NotNull @Min(1) @Max(10) Integer creditos,
            @NotBlank @Size(max = 10) String periodo) {}

    public record CursoResponse(
            Long id, String codigo, String nombre, Integer creditos,
            String periodo, String profesor) {}

    // ---------- Matrículas ----------
    public record MatriculaRequest(
            @NotNull Long estudianteId,
            @NotNull Long cursoId) {}

    public record MatriculaResponse(
            Long id, Long estudianteId, Long cursoId, LocalDate fecha) {}

    // ---------- Calificaciones ----------
    public record CalificacionRequest(
            @NotNull Long matriculaId,
            @NotBlank @Size(max = 100) String descripcion,
            @NotNull @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal nota,
            @NotNull @DecimalMin("0.01") @DecimalMax("100.0") BigDecimal porcentaje,
            @Size(max = 500) String observacion) {}

    public record CalificacionResponse(
            Long id, Long matriculaId, String descripcion, BigDecimal nota,
            BigDecimal porcentaje, String observacion, LocalDate fecha) {}

    // ---------- Vistas compuestas ----------
    /** Vista del profesor: un estudiante dentro de un curso con sus notas. */
    public record EstudianteCursoResponse(
            Long matriculaId, EstudianteResponse estudiante,
            List<CalificacionResponse> calificaciones, BigDecimal definitiva) {}

    /** Vista del estudiante: un curso con sus notas. */
    public record NotaCurso(
            Long cursoId, String codigo, String curso, Integer creditos,
            String profesor, List<CalificacionResponse> calificaciones,
            BigDecimal porcentajeEvaluado, BigDecimal definitiva) {}

    /** Panel completo del estudiante. */
    public record PanelEstudiante(
            EstudianteResponse perfil, List<NotaCurso> cursos,
            BigDecimal promedioGeneral, int totalCreditos) {}
}
