package eus.tartanga.psp.server.model;

public class VideoItem {
    private Long id;
    private String titulo;
    private String descripcion;
    private String nombreArchivo;
    private String urlYoutube;

    // Constructor vacío necesario para Jackson
    public VideoItem() {
    }

    public VideoItem(Long id, String titulo, String descripcion, String nombreArchivo, String urlYoutube) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
        this.urlYoutube = urlYoutube;
    }

    // Getters y Setters
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    // --- GETTERS Y SETTERS NUEVOS ---
    public String getUrlYoutube() {
        return urlYoutube;
    }

    public void setUrlYoutube(String urlYoutube) {
        this.urlYoutube = urlYoutube;
    }
}