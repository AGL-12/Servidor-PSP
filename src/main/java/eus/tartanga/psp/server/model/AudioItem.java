package eus.tartanga.psp.server.model;

public class AudioItem {
	private Long id;
    private String titulo;
    private String descripcion;
    private String nombreArchivo; // Ej: "musica_fondo.mp3"

    public AudioItem() {}
    public AudioItem(Long id, String titulo, String descripcion, String nombreArchivo) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
    }
    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
}
