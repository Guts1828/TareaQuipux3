package co.edu.uco.academico.service;

import co.edu.uco.academico.dto.Dtos.*;
import co.edu.uco.academico.exception.ApiException;
import co.edu.uco.academico.model.Calificacion;
import co.edu.uco.academico.model.Estudiante;
import co.edu.uco.academico.model.Matricula;
import co.edu.uco.academico.repository.CalificacionRepository;
import co.edu.uco.academico.repository.EstudianteRepository;
import co.edu.uco.academico.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstudianteService {

    private final EstudianteRepository estudiantes;
    private final MatriculaRepository matriculas;
    private final CalificacionRepository calificaciones;

    /** Panel completo: perfil, cursos con notas, promedio ponderado por créditos. */
    public PanelEstudiante panel(String email) {
        Estudiante e = estudiante(email);
        List<NotaCurso> cursos = matriculas.findByEstudianteId(e.getId()).stream()
                .map(this::notaCurso).toList();

        int creditos = cursos.stream().mapToInt(NotaCurso::creditos).sum();
        BigDecimal promedio = BigDecimal.ZERO.setScale(2);
        if (creditos > 0) {
            promedio = cursos.stream()
                    .map(c -> c.definitiva().multiply(BigDecimal.valueOf(c.creditos())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(creditos), 2, RoundingMode.HALF_UP);
        }
        return new PanelEstudiante(Mapper.estudiante(e), cursos, promedio, creditos);
    }

    public List<NotaCurso> misCursos(String email) {
        return matriculas.findByEstudianteId(estudiante(email).getId()).stream()
                .map(this::notaCurso).toList();
    }

    public NotaCurso notasDeCurso(String email, Long cursoId) {
        Matricula m = matriculas.findByEstudianteIdAndCursoId(estudiante(email).getId(), cursoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No estás matriculado en ese curso"));
        return notaCurso(m);
    }

    private NotaCurso notaCurso(Matricula m) {
        List<Calificacion> notas = calificaciones.findByMatriculaId(m.getId());
        var c = m.getCurso();
        return new NotaCurso(c.getId(), c.getCodigo(), c.getNombre(), c.getCreditos(),
                c.getProfesor().getNombres() + " " + c.getProfesor().getApellidos(),
                notas.stream().map(Mapper::calificacion).toList(),
                Mapper.porcentajeEvaluado(notas), Mapper.definitiva(notas));
    }

    private Estudiante estudiante(String email) {
        return estudiantes.findByUsuarioEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Estudiante no encontrado"));
    }
}
