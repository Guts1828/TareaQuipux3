package co.edu.uco.academico.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "calificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false, length = 100)
    private String descripcion;

    /** Nota en escala colombiana 0.0 - 5.0 */
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal nota;

    /** Porcentaje que aporta a la nota definitiva (0 - 100) */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(length = 500)
    private String observacion;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();
}
