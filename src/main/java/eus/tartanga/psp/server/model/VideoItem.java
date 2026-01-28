package eus.tartanga.psp.server.model;

public class VideoItem {
	private Long id;
	private String titulo;
	private String descripcion;
	private String urlYoutube; // Ej: "https://youtu.be/..."

	public VideoItem() {
	}

	public VideoItem(Long id, String titulo, String descripcion, String urlYoutube) {
		this.id = id;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.urlYoutube = urlYoutube;
	}

	// Getters y Setters...
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

	public String getUrlYoutube() {
		return urlYoutube;
	}

	public void setUrlYoutube(String urlYoutube) {
		this.urlYoutube = urlYoutube;
	}
}
