package com.alura.literalura.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String nombre;
    private Integer fechaDeNacimiento;
    private Integer fechaDeFallecimiento;

    @OneToOne
    @JsonManagedReference
    private Libro libro;

    @Override
    public String toString() {
        return "EntidadAutor{" +
                "fechaDeFallecimiento='" + fechaDeFallecimiento + '\'' +
                ", fechaDeNacimiento='" + fechaDeNacimiento + '\'' +
                ", nombre='" + nombre + '\'' +
                ", id=" + id +
                '}';
    }
}
