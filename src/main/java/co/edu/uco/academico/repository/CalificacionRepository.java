package co.edu.uco.academico.repository;

import co.edu.uco.academico.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByMatriculaId(Long matriculaId);
}
