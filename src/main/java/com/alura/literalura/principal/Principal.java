package com.alura.literalura.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.alura.literalura.literaluraDto.General;
import com.alura.literalura.literaluraDto.DatosLibro;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConvierteDatos convierteDatos = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);

    public void mostrarMenu() {
        try {
            var json = consumoApi.obtenerDatos(URL_BASE);
            var datos = convierteDatos.obtenerDatos(json, General.class);

            int opcion = -1;
            while (opcion != 0) {
                String menu =
                        """
                        ESCRIBA EL NÚMERO DE LA OPCIÓN!
                        ********************************************
                        1- Buscar libro por título
                        2- Listar libros registrados
                        3- Listar autores registrados
                        4- Listar autores vivos en un determinado año
                        5- Listar libros por idioma
                        0- Salir
                        ********************************************
                        """;
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo();
                    case 2 -> listarLibrosRegistrados(datos);
                    case 3 -> listarAutoresRegistrados(datos);
                    case 4 -> listarAutoresVivosPorFecha();
                    case 5 -> listarLibrosPorIdioma(datos);
                    case 0 -> salir();
                    default -> System.out.println("Opción inválida. Por favor, selecciona una opción válida.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al ejecutar el menú: " + e.getMessage());
        }
    }
// Método para buscar libro por título
private void buscarLibroPorTitulo() {
    try {
        System.out.println("\nIngrese el nombre del libro que desea buscar:");
        var tituloLibro = teclado.nextLine().trim();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = convierteDatos.obtenerDatos(json, General.class);

        var libroBuscado = datosBusqueda.listaLibros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado:");
            System.out.println("Título: " + libroBuscado.get().titulo());

            libroBuscado.get().autor().forEach(autor ->
                    System.out.println("Autor: " + autor.nombre())
            );

            System.out.println("Idiomas: " + libroBuscado.get().idioma());

            System.out.println("Número de descargas: " + libroBuscado.get().numeroDeDescargas());

            System.out.println("\n-----------------------------------------");

        } else {
            System.out.println("Libro no encontrado.");
        }
    } catch (Exception e) {
        System.err.println("Error al buscar libro: " + e.getMessage());
    }
}


    // Método para mostrar lista de los libros registrados
    private void listarLibrosRegistrados(General datos) {
        System.out.println("\nListado de libros registrados:");
        datos.listaLibros().forEach(libro -> {
            System.out.println("- Título: " + libro.titulo());
            // Si autor es una lista, iteramos sobre ella
            libro.autor().forEach(autor -> System.out.println("- Autor: " + autor.nombre()));
            System.out.println("- Idioma: " + libro.idioma());
            System.out.println("- Número de descargas: " + libro.numeroDeDescargas());
            System.out.println("\n--------------------------------------------");
        });

    }


    // Método para mostrar lista de autores y sus parametros registrados
    private void listarAutoresRegistrados(General datos) {
        System.out.println("\nListado de autores registrados:");

        datos.listaLibros().stream()
                .flatMap(libro -> libro.autor().stream().map(autor -> Map.entry(autor, libro.titulo()))) // Asociar autor con títulos
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList()))) // Agrupar libros por autor
                .forEach((autor, libros) -> {
                    System.out.println("Nombre: " + autor.nombre());
                    System.out.println("Fecha de Nacimiento: " +
                            (autor.fechaDeNacimiento() != null ? autor.fechaDeNacimiento() : "Desconocida"));
                    System.out.println("Fecha de Fallecimiento: " +
                            (autor.fechaDeFallecimiento() != null ? autor.fechaDeFallecimiento() : "Desconocida"));

                    // Mostrar los títulos de los libros asociados al autor
                    if (libros != null && !libros.isEmpty()) {
                        System.out.println("Libros:");
                        libros.forEach(libro -> System.out.println("  - " + libro));
                    } else {
                        System.out.println("Libros: No hay información disponible.");
                    }

                    System.out.println("\n--------------------------------------------");
                });
    }

// Método para mostrar lista de autores vivos en determinado periodo de tiempo
    private void listarAutoresVivosPorFecha() {
        try {
            // Solicitar y validar fecha inicial
            LocalDate fechaInicio = leerFechaUsuario("Fecha inicial (dd/MM/yyyy): ");

            // Solicitar y validar fecha final
            LocalDate fechaFin = leerFechaUsuario("Fecha final (dd/MM/yyyy): ");

            // Obtener datos de la API
            var json = consumoApi.obtenerDatos(URL_BASE
                    + "?author_year_start=" + fechaInicio.getYear()
                    + "&author_year_end=" + fechaFin.getYear());
            var datosBusqueda = convierteDatos.obtenerDatos(json, General.class);

            var autoresFiltrados = datosBusqueda.listaLibros().stream()
                    .flatMap(libro -> libro.autor().stream())
                    .filter(autor -> autor.fechaDeNacimiento() != null)
                    .filter(autor -> {
                        int year = autor.fechaDeNacimiento();
                        return year >= fechaInicio.getYear() && year <= fechaFin.getYear();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            // Imprimir resultados
            System.out.println("\nAutores vivos en el rango de fechas:");
            autoresFiltrados.forEach(autor -> System.out.println("Autor: " + autor.nombre()));
        } catch (Exception e) {
            System.err.println("Error al filtrar autores por fecha: " + e.getMessage());
        }
    }

    // Método auxiliar para leer y validar fechas
    private LocalDate leerFechaUsuario(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String entrada = teclado.nextLine().trim();

                // Intentar parsear la fecha en formato esperado
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(entrada.replace("-", "/"), formatter);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Por favor, use el formato dd/MM/yyyy.");

            }
            System.out.println("\n--------------------------------------------");
        }
    }

// Método para buscar los libros escritos en uno de los idiomas listados
    private void listarLibrosPorIdioma(General datos) {
        //System.out.println("\nListado de libros por idioma:");

        // Solicitar al usuario el idioma
        System.out.println("Ingrese la sigla del idioma:");
        System.out.println("- es  (español)");
        System.out.println("- en  (inglés)");
        System.out.println("- fr  (francés)");
        System.out.println("- pt  (portugués)");

        String idiomaBuscado = teclado.nextLine().trim().toLowerCase(); // Leer la entrada y convertir a minúsculas

        // Filtrar y mostrar los libros en el idioma seleccionado
        List<DatosLibro> librosFiltrados = datos.listaLibros().stream()
                .filter(libro -> libro.idioma() != null && libro.idioma().contains(idiomaBuscado)) // Filtrar libros por idioma
                .collect(Collectors.toList());

        // Verificar si hay resultados
        if (librosFiltrados.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma " + idiomaBuscado);
        } else {
            System.out.println("\nLibros disponibles en el idioma: " + idiomaBuscado);
            librosFiltrados.forEach(libro -> {
                System.out.println("Título: " + libro.titulo());
                System.out.println("Idiomas disponibles: " + String.join(", ", libro.idioma()));
                System.out.println("\n--------------------------------------------");
            });

            }
    }
    //Metodo para salir del menú
    private void salir(){
        System.out.println("Saliendo...");
        System.exit(0);
    }
}







