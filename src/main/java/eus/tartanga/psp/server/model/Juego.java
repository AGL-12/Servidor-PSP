package eus.tartanga.psp.server.model;

import java.io.Serializable;

public class Juego implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// Atributos obligatorios para el Reto
    private Long id;                // Identificador único
    private String titulo;          // Título del juego
    private String descripcion;     // Descripción corta
    private String rutaApk;         // Nombre del archivo .apk (ej: "mi_juego.apk")
    private String rutaImagen;      // Nombre de la imagen (ej: "caratula.png")
    private String hash;            // El hash SHA-256 (Requisito de PSP)

    // 1. Constructor vacío (Obligatorio para que funcione Spring Boot)
    public Juego() {
    }

    // 2. Constructor con todos los campos (Para crear juegos fácilmente)
    public Juego(Long id, String titulo, String descripcion, String rutaApk, String rutaImagen, String hash) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.rutaApk = rutaApk;
        this.rutaImagen = rutaImagen;
        this.hash = hash;
    }

    // 3. Getters y Setters (Imprescindibles)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaApk() {
        return rutaApk;
    }

    public void setRutaApk(String rutaApk) {
        this.rutaApk = rutaApk;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    // Método toString() para ver los datos en la consola si hace falta
    @Override
    public String toString() {
        return "Juego [titulo=" + titulo + ", apk=" + rutaApk + ", hash=" + hash + "]";
    }
}