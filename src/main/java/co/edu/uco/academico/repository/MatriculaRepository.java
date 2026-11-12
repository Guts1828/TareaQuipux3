package co.edu.uco.academico.repository;

import co.edu.uco.academico.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByEstudianteId(Long estudianteId);
    List<Matricula> findByCursoId(Long cursoId);
    boolean existsByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);
    Optional<Matricula> findByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);
}
