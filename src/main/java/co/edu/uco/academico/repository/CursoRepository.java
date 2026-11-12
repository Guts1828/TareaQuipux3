package co.edu.uco.academico.repository;

import co.edu.uco.academico.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByProfesorId(Long profesorId);
    boolean existsByCodigo(String codigo);
}
