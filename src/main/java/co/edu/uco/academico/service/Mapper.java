package co.edu.uco.academico.service;

import co.edu.uco.academico.dto.Dtos.*;
import co.edu.uco.academico.model.Calificacion;
import co.edu.uco.academico.model.Curso;
import co.edu.uco.academico.model.Estudiante;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Conversión entidad -> DTO y cálculo de notas. */
final class Mapper {

    private Mapper() {}

    static EstudianteResponse estudiante(Estudiante e) {
        return new EstudianteResponse(e.getId(), e.getCodigo(), e.getNombres(), e.getApellidos(),
                e.getUsuario().getEmail(), e.getPrograma(), e.getSemestre());
    }

    static CursoResponse curso(Curso c) {
        return new CursoResponse(c.getId(), c.getCodigo(), c.getNombre(), c.getCreditos(),
                c.getPeriodo(), c.getProfesor().getNombres() + " " + c.getProfesor().getApellidos());
    }

    static CalificacionResponse calificacion(Calificacion c) {
        return new CalificacionResponse(c.getId(), c.getMatricula().getId(), c.getDescripcion(),
                c.getNota(), c.getPorcentaje(), c.getObservacion(), c.getFecha());
    }

    /** Nota definitiva = suma(nota * porcentaje / 100). */
    static BigDecimal definitiva(List<Calificacion> notas) {
        return notas.stream()
                .map(n -> n.getNota().multiply(n.getPorcentaje()).divide(BigDecimal.valueOf(100)))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal porcentajeEvaluado(List<Calificacion> notas) {
        return notas.stream().map(Calificacion::getPorcentaje).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
