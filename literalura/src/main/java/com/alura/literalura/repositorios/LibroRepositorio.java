package com.alura.literalura.repositorios;

import com.alura.literalura.modelo.Libro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepositorio extends JpaRepository<Libro, Long> {
    @Query("SELECT i FROM EntidadLibro i WHERE i.idioma = :idioma")
    List<Libro> getEntidadLibroByIdioma(@Param("idioma") String idioma);

}


