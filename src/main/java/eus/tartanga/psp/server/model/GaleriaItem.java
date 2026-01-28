package eus.tartanga.psp.server.model;

import java.util.List;

public class GaleriaItem {
	private Long id;
    private String titulo;
    private String descripcion;
    private List<String> imagenes; // Ej: ["foto1.jpg", "foto2.jpg"]

    public GaleriaItem() {}
    public GaleriaItem(Long id, String titulo, String descripcion, List<String> imagenes) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenes = imagenes;
    }
    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
}
