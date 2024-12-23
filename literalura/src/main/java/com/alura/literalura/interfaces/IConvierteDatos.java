package com.alura.literalura.interfaces;

public interface IConvierteDatos {
    <T> T obtenerDatos (String json, Class <T> clase);
}
