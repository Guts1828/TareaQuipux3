package co.edu.uco.academico.config;

import co.edu.uco.academico.model.*;
import co.edu.uco.academico.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Datos de ejemplo para poder probar la API al primer arranque (solo si no hay usuarios). */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarios;
    private final ProfesorRepository profesores;
    private final EstudianteRepository estudiantes;
    private final CursoRepository cursos;
    private final MatriculaRepository matriculas;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;

        Usuario up = usuario("profesor@uco.edu.co", "profesor123", Rol.PROFESOR);
        Profesor p = new Profesor();
        p.setNombres("Carlos");
        p.setApellidos("Ramírez");
        p.setDepartamento("Ingeniería de Sistemas");
        p.setUsuario(up);
        p = profesores.save(p);

        Usuario ue = usuario("estudiante@uco.edu.co", "estudiante123", Rol.ESTUDIANTE);
        Estudiante e = new Estudiante();
        e.setCodigo("2026001");
        e.setNombres("Laura");
        e.setApellidos("Gómez");
        e.setPrograma("Ingeniería de Sistemas");
        e.setSemestre(5);
        e.setUsuario(ue);
        e = estudiantes.save(e);

        Curso c = new Curso();
        c.setCodigo("SIS-301");
        c.setNombre("Programación Avanzada");
        c.setCreditos(3);
        c.setPeriodo("2026-2");
        c.setProfesor(p);
        c = cursos.save(c);

        Matricula m = new Matricula();
        m.setEstudiante(e);
        m.setCurso(c);
        matriculas.save(m);

        log.info("Datos demo creados -> profesor@uco.edu.co / profesor123  |  estudiante@uco.edu.co / estudiante123");
    }

    private Usuario usuario(String email, String pass, Rol rol) {
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setPassword(encoder.encode(pass));
        u.setRol(rol);
        return usuarios.save(u);
    }
}
