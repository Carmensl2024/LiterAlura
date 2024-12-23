package com.alura.literalura.literaluraDto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public record General(
        @JsonAlias("results") List<DatosLibro> listaLibros
) {
}
