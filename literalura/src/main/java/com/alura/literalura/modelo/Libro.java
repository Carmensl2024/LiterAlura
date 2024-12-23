package com.alura.literalura.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String titulo;
    @OneToOne (mappedBy = "libro")
    @JsonBackReference
    private Autor autor;
    private String idioma;
    private Double numeroDeDescargas;


}
