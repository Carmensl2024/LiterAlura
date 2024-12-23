package com.alura.literalura.repositorios;

import com.alura.literalura.modelo.Autor;
import com.alura.literalura.modelo.Libro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepositorio extends JpaRepository<Autor, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE EntidadAutor a SET a.LIBRO= :newLibro WHERE a.id= :id")
    void updateEntidadAutorBy(@Param("newLibro") Libro libro, @Param("id") Long id);

    @Query("SELECT y FROM EntidadAutor y WHERE y.fechaDeFallecimiento > :year AND y.fechaDeNacimiento < :year")
    List<Autor> getEntidadAutorByFechaDeNacimiento(@Param("year") Integer year);

    @Query("SELECT a FROM EntidadAutor a WHERE a.nombre = :nombre")
    Optional<Autor> getAutorByNombre(@Param("nombre") String nombre);
}

