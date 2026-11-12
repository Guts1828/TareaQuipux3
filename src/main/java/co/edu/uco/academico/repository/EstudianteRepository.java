package co.edu.uco.academico.repository;

import co.edu.uco.academico.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByUsuarioEmail(String email);
    boolean existsByCodigo(String codigo);
}
