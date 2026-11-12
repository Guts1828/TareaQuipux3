package co.edu.uco.academico.service;

import co.edu.uco.academico.dto.Dtos.*;
import co.edu.uco.academico.exception.ApiException;
import co.edu.uco.academico.model.*;
import co.edu.uco.academico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfesorService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final UsuarioRepository usuarios;
    private final EstudianteRepository estudiantes;
    private final ProfesorRepository profesores;
    private final CursoRepository cursos;
    private final MatriculaRepository matriculas;
    private final CalificacionRepository calificaciones;
    private final PasswordEncoder passwordEncoder;

    // ---------- Estudiantes ----------
    public EstudianteResponse registrarEstudiante(EstudianteRequest req) {
        if (usuarios.existsByEmail(req.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        if (estudiantes.existsByCodigo(req.codigo()))
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un estudiante con ese código");

        Usuario u = new Usuario();
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRol(Rol.ESTUDIANTE);
        u = usuarios.save(u);

        Estudiante e = new Estudiante();
        e.setCodigo(req.codigo());
        e.setNombres(req.nombres());
        e.setApellidos(req.apellidos());
        e.setPrograma(req.programa());
        e.setSemestre(req.semestre());
        e.setUsuario(u);
        return Mapper.estudiante(estudiantes.save(e));
    }

    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarEstudiantes() {
        return estudiantes.findAll().stream().map(Mapper::estudiante).toList();
    }

    // ---------- Cursos ----------
    public CursoResponse crearCurso(String emailProfesor, CursoRequest req) {
        Profesor p = profesor(emailProfesor);
        if (cursos.existsByCodigo(req.codigo()))
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un curso con ese código");
        Curso c = new Curso();
        c.setCodigo(req.codigo());
        c.setNombre(req.nombre());
        c.setCreditos(req.creditos());
        c.setPeriodo(req.periodo());
        c.setProfesor(p);
        return Mapper.curso(cursos.save(c));
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> misCursos(String emailProfesor) {
        return cursos.findByProfesorId(profesor(emailProfesor).getId()).stream().map(Mapper::curso).toList();
    }

    // ---------- Matrículas ----------
    public MatriculaResponse matricular(String emailProfesor, MatriculaRequest req) {
        Curso curso = cursoPropio(emailProfesor, req.cursoId());
        Estudiante est = estudiantes.findById(req.estudianteId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        if (matriculas.existsByEstudianteIdAndCursoId(est.getId(), curso.getId()))
            throw new ApiException(HttpStatus.CONFLICT, "El estudiante ya está matriculado en el curso");

        Matricula m = new Matricula();
        m.setEstudiante(est);
        m.setCurso(curso);
        m.setFecha(LocalDate.now());
        m = matriculas.save(m);
        return new MatriculaResponse(m.getId(), est.getId(), curso.getId(), m.getFecha());
    }

    @Transactional(readOnly = true)
    public List<EstudianteCursoResponse> estudiantesDeCurso(String emailProfesor, Long cursoId) {
        cursoPropio(emailProfesor, cursoId);
        return matriculas.findByCursoId(cursoId).stream().map(m -> {
            List<Calificacion> notas = calificaciones.findByMatriculaId(m.getId());
            return new EstudianteCursoResponse(m.getId(), Mapper.estudiante(m.getEstudiante()),
                    notas.stream().map(Mapper::calificacion).toList(), Mapper.definitiva(notas));
        }).toList();
    }

    // ---------- Calificaciones ----------
    public CalificacionResponse calificar(String emailProfesor, CalificacionRequest req) {
        Matricula m = matriculaPropia(emailProfesor, req.matriculaId());
        validarPorcentaje(m.getId(), null, req.porcentaje());

        Calificacion c = new Calificacion();
        c.setMatricula(m);
        aplicar(c, req);
        return Mapper.calificacion(calificaciones.save(c));
    }

    public CalificacionResponse actualizarCalificacion(String emailProfesor, Long id, CalificacionRequest req) {
        Calificacion c = calificaciones.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Calificación no encontrada"));
        matriculaPropia(emailProfesor, c.getMatricula().getId());
        validarPorcentaje(c.getMatricula().getId(), c.getId(), req.porcentaje());
        aplicar(c, req);
        return Mapper.calificacion(calificaciones.save(c));
    }

    public void eliminarCalificacion(String emailProfesor, Long id) {
        Calificacion c = calificaciones.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Calificación no encontrada"));
        matriculaPropia(emailProfesor, c.getMatricula().getId());
        calificaciones.delete(c);
    }

    // ---------- Helpers ----------
    private void aplicar(Calificacion c, CalificacionRequest req) {
        c.setDescripcion(req.descripcion());
        c.setNota(req.nota());
        c.setPorcentaje(req.porcentaje());
        c.setObservacion(req.observacion());
        c.setFecha(LocalDate.now());
    }

    /** La suma de porcentajes de una matrícula no puede superar 100. */
    private void validarPorcentaje(Long matriculaId, Long excluirId, BigDecimal nuevo) {
        BigDecimal usado = calificaciones.findByMatriculaId(matriculaId).stream()
                .filter(c -> !c.getId().equals(excluirId))
                .map(Calificacion::getPorcentaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (usado.add(nuevo).compareTo(CIEN) > 0)
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La suma de porcentajes superaría 100 (ya evaluado: " + usado + "%)");
    }

    private Profesor profesor(String email) {
        return profesores.findByUsuarioEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Profesor no encontrado"));
    }

    private Curso cursoPropio(String email, Long cursoId) {
        Profesor p = profesor(email);
        Curso c = cursos.findById(cursoId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
        if (!c.getProfesor().getId().equals(p.getId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Ese curso no pertenece a este profesor");
        return c;
    }

    private Matricula matriculaPropia(String email, Long matriculaId) {
        Matricula m = matriculas.findById(matriculaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Matrícula no encontrada"));
        cursoPropio(email, m.getCurso().getId());
        return m;
    }
}
