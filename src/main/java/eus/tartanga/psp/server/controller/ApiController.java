package eus.tartanga.psp.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import eus.tartanga.psp.server.model.AudioItem;
import eus.tartanga.psp.server.model.GaleriaItem;
import eus.tartanga.psp.server.model.Juego;
import eus.tartanga.psp.server.model.VideoItem;
import eus.tartanga.psp.server.service.ServiceArchivos;

@RestController
public class ApiController {

	@Autowired
	private ServiceArchivos serviceArchivos;

	// 1. Listar Juegos (Llama al método que sincroniza JSON + ProcessBuilder)
	@GetMapping("/api/juegos")
	public ResponseEntity<List<Juego>> listarJuegos() {
		return ResponseEntity.ok(serviceArchivos.listarJuegos());
	}
	@GetMapping("/api/videos")
    public ResponseEntity<List<VideoItem>> listarVideos() {
        return ResponseEntity.ok(serviceArchivos.listarVideos());
    }

    // 2. Endpoint Audios
    @GetMapping("/api/audios")
    public ResponseEntity<List<AudioItem>> listarAudios() {
        return ResponseEntity.ok(serviceArchivos.listarAudios());
    }

    // 3. Endpoint Galeria
    @GetMapping("/api/galeria")
    public ResponseEntity<List<GaleriaItem>> listarGaleria() {
        return ResponseEntity.ok(serviceArchivos.listarGaleria());
    }

	// 2. Descargar Fichero (APK o Imagen)
	@GetMapping("/api/files/{nombre:.+}")
	public ResponseEntity<Resource> descargarFichero(@PathVariable String nombre) {

		// Validación básica
		if (nombre == null || nombre.trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		// Llamamos al método del servicio (asegúrate de que se llama igual en el
		// Service)
		Resource recurso = serviceArchivos.cargarRecurso(nombre);

		if (recurso == null || !recurso.exists() || !recurso.isReadable()) {
			return ResponseEntity.notFound().build();
		}

		// Definimos Content-Type
		String contentType = "application/octet-stream";
		if (nombre.endsWith(".apk")) {
			contentType = "application/vnd.android.package-archive";
		} else if (nombre.endsWith(".png")) {
			contentType = "image/png";
		} else if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
			contentType = "image/jpeg";
		}else if (nombre.endsWith(".mp4")) {
			contentType = "video/mp4";
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	// --- ZONA DE GESTIÓN DE DESCRIPCIONES (CRUD) ---

	// 1. CREAR (POST): Solo funciona si no había descripción antes
	@PostMapping("/api/juegos/{nombre:.+}/descripcion")
	public ResponseEntity<String> crearDescripcion(@PathVariable String nombre, @RequestBody String texto) {
		boolean exito = serviceArchivos.crearDescripcion(nombre, texto);
		if (exito) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Descripción creada.");
		} else {
			// Devolvemos 409 (Conflict) si ya existía una descripción o 404 si no existe el
			// juego
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Error: El juego no existe o YA TIENE descripción (usa PUT para editar).");
		}
	}

	// 2. ACTUALIZAR (PUT): Modifica la descripción existente
	@PutMapping("/api/juegos/{nombre:.+}/descripcion")
	public ResponseEntity<String> actualizarDescripcion(@PathVariable String nombre, @RequestBody String texto) {
		boolean exito = serviceArchivos.actualizarDescripcion(nombre, texto);
		if (exito) {
			return ResponseEntity.ok("Descripción actualizada.");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 3. BORRAR (DELETE): Reinicia la descripción a "Sin descripción"
	@DeleteMapping("/api/juegos/{nombre:.+}/descripcion")
	public ResponseEntity<String> borrarDescripcion(@PathVariable String nombre) {
		boolean exito = serviceArchivos.borrarDescripcion(nombre);
		if (exito) {
			return ResponseEntity.ok("Descripción eliminada (reseteada).");
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}