package eus.tartanga.psp.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eus.tartanga.psp.server.model.AudioItem;
import eus.tartanga.psp.server.model.GaleriaItem;
import eus.tartanga.psp.server.model.Juego;
import eus.tartanga.psp.server.model.VideoItem;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class ServiceArchivos {

	@Value("${media.location}")
	private String rutaBase;

	// Archivo donde guardaremos los objetos serializados (descripciones, etc.)
	private final String ARCHIVO_DATOS = "datos_juegos.json";

	// Herramienta de Spring para convertir Objetos <-> JSON
	private ObjectMapper mapper = new ObjectMapper();

	// Nuestra base de datos en RAM
	private Map<String, Juego> mapaJuegos = new ConcurrentHashMap<>();

	@PostConstruct
	public void iniciar() {
		System.out.println("--- 🚀 INICIANDO SERVIDOR (MODO JSON + PROCESSBUILDER) ---");
		cargarDatosJson(); // 1. Cargamos el JSON
		sincronizarConDisco(); // 2. Actualizamos con los archivos reales
	}

	@PreDestroy
	public void alCerrar() {
		guardarDatosJson();
	}

	public List<Juego> listarJuegos() {
		sincronizarConDisco();

		// Convertir Mapa a Lista (Sin lambdas)
		List<Juego> lista = new ArrayList<>();
		for (Juego j : mapaJuegos.values()) {
			lista.add(j);
		}
		return lista;
	}
	public List<VideoItem> listarVideos() {
        File f = new File(rutaBase,"datos_videos.json");
        if (f.exists()) {
            try {
                return mapper.readValue(f, new TypeReference<List<VideoItem>>() {});
            } catch (IOException e) { e.printStackTrace(); }
        }
        return new ArrayList<>(); // Lista vacía si falla
    }

    public List<AudioItem> listarAudios() {
        File f = new File(rutaBase,"datos_audios.json");
        if (f.exists()) {
            try {
                return mapper.readValue(f, new TypeReference<List<AudioItem>>() {});
            } catch (IOException e) { e.printStackTrace(); }
        }
        return new ArrayList<>();
    }

    public List<GaleriaItem> listarGaleria() {
        File f = new File(rutaBase,"datos_galeria.json");
        if (f.exists()) {
            try {
                return mapper.readValue(f, new TypeReference<List<GaleriaItem>>() {});
            } catch (IOException e) { e.printStackTrace(); }
        }
        return new ArrayList<>();
    }

	// =========================================================
	// PARTE 1: PROCESS BUILDER (SOLO PARA LISTAR ARCHIVOS)
	// =========================================================
	// Este método usa 'dir /b' (Windows) o 'ls' (Linux) para obtener los nombres
	private List<String> obtenerNombresConProcessBuilder(File carpeta) {
		List<String> nombresEncontrados = new ArrayList<>();
		List<String> comandos = new ArrayList<>();

		boolean esWindows = System.getProperty("os.name").toLowerCase().contains("win");

		if (esWindows) {
			// "cmd /c dir /b" -> /b significa "bare" (solo nombres, sin fechas ni tamaños)
			comandos.add("cmd.exe");
			comandos.add("/c");
			comandos.add("dir");
			comandos.add("/b");
			comandos.add(carpeta.getAbsolutePath());
		} else {
			comandos.add("ls");
			comandos.add(carpeta.getAbsolutePath());
		}

		try {
			ProcessBuilder pb = new ProcessBuilder(comandos);
			Process proceso = pb.start();
			InputStream is = proceso.getInputStream();

			// Leemos byte a byte manualmente (sin BufferedReader, como pediste)
			StringBuilder sb = new StringBuilder();
			int letra;
			while ((letra = is.read()) != -1) {
				sb.append((char) letra);
			}

			proceso.waitFor();

			// Convertimos la salida bruta en una lista de nombres
			// Separamos por saltos de línea
			String salidaCompleta = sb.toString();
			String[] lineas = salidaCompleta.split("\\r?\\n");

			for (String linea : lineas) {
				if (!linea.trim().isEmpty()) {
					nombresEncontrados.add(linea.trim());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("⚠️ Falló ProcessBuilder, usando método nativo de respaldo.");
			// Si falla, devolvemos null y que sincronizarConDisco use el método normal
			return null;
		}
		return nombresEncontrados;
	}

	// =========================================================
	// PERSISTENCIA JSON (JACKSON)
	// =========================================================
	private void cargarDatosJson() {
		File f = new File(rutaBase, ARCHIVO_DATOS);
		if (f.exists()) {
			try {
				this.mapaJuegos = mapper.readValue(f, new TypeReference<Map<String, Juego>>() {
				});
				System.out.println("📥 JSON cargado: " + mapaJuegos.size() + " juegos.");
			} catch (IOException e) {
				this.mapaJuegos = new ConcurrentHashMap<>();
			}
		}
	}

	private void guardarDatosJson() {
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaBase, ARCHIVO_DATOS), mapaJuegos);
			System.out.println("💾 JSON guardado.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================
	// PARTE 2: HASH NATIVO (EL TUYO, EL BUENO)
	// =========================================================
	private String calcularHash(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] buffer = new byte[8192]; // Buffer de 8KB es un poco más rápido que 1024
			int nread;
			while ((nread = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, nread);
			}
			StringBuilder sb = new StringBuilder();
			for (byte b : digest.digest()) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR_HASH";
		}
	}

	// =========================================================
	// PARTE 3: SINCRONIZACIÓN (INTEGRA TODO)
	// =========================================================
	private void sincronizarConDisco() {
		File carpetaApks = new File(rutaBase + "apks");
		if (!carpetaApks.exists())
			return;

		// --- AQUI USAMOS EL PROCESS BUILDER ---
		List<String> nombresArchivos = obtenerNombresConProcessBuilder(carpetaApks);

		// Si ProcessBuilder falla por lo que sea, usamos Java normal de respaldo
		if (nombresArchivos == null) {
			nombresArchivos = new ArrayList<>();
			File[] files = carpetaApks.listFiles();
			if (files != null) {
				for (File f : files)
					nombresArchivos.add(f.getName());
			}
		}

		// Filtramos solo los que acaban en .apk
		List<String> apksReales = new ArrayList<>();
		for (String nombre : nombresArchivos) {
			if (nombre.endsWith(".apk")) {
				apksReales.add(nombre);
			}
		}

		// 1. Borrar lo que no existe
		Iterator<String> it = mapaJuegos.keySet().iterator();
		boolean cambios = false;
		while (it.hasNext()) {
			String nombreMemoria = it.next();
			boolean existe = false;
			for (String real : apksReales) {
				if (real.equals(nombreMemoria)) {
					existe = true;
					break;
				}
			}
			if (!existe) {
				it.remove();
				System.out.println("🗑️ Borrado: " + nombreMemoria);
				cambios = true;
			}
		}

		// 2. Añadir lo nuevo
		for (String nombreReal : apksReales) {
			if (!mapaJuegos.containsKey(nombreReal)) {
				System.out.println("🆕 Nuevo detectado: " + nombreReal);

				File archivo = new File(carpetaApks, nombreReal);

				// USAMOS TU MÉTODO DE HASH SEGURO
				String hash = calcularHash(archivo);

				String titulo = nombreReal.replace(".apk", "").toUpperCase();
				String img = nombreReal.replace(".apk", ".png");

				Juego j = new Juego(System.currentTimeMillis(), titulo, "Sin descripción", nombreReal, img, hash);
				mapaJuegos.put(nombreReal, j);
				cambios = true;
			}
		}

		if (cambios)
			guardarDatosJson();
	}

	// =========================================================
	// CRUD DESCRIPCIONES Y DESCARGA
	// =========================================================
	public boolean crearDescripcion(String nombre, String texto) {
		Juego j = mapaJuegos.get(nombre);
		if (j != null && (j.getDescripcion().equals("Sin descripción") || j.getDescripcion().isEmpty())) {
			j.setDescripcion(texto);
			guardarDatosJson();
			return true;
		}
		return false;
	}

	public boolean actualizarDescripcion(String nombre, String texto) {
		Juego j = mapaJuegos.get(nombre);
		if (j != null) {
			j.setDescripcion(texto);
			guardarDatosJson();
			return true;
		}
		return false;
	}

	public boolean borrarDescripcion(String nombre) {
		Juego j = mapaJuegos.get(nombre);
		if (j != null) {
			j.setDescripcion("Sin descripción");
			guardarDatosJson();
			return true;
		}
		return false;
	}

	public Resource cargarRecurso(String nombre) {
		String sub = "";

		// Lógica para decidir en qué carpeta buscar
		if (nombre.endsWith(".apk")) {
			sub = "apks";
		} else if (nombre.endsWith(".mp3") || nombre.endsWith(".wav")) {
			sub = "audios"; // <--- NUEVA CARPETA
		}else if (nombre.endsWith(".mp4") || nombre.endsWith(".mov") || nombre.endsWith(".avi")) {
			sub = "videos"; // <--- NUEVA LÓGICA PARA VIDEOS
		} else {
			// Si es imagen, puede ser de un juego (carpeta imagenes) o de la galería
			// (carpeta galeria_files)
			// Primero probamos en "imagenes" (carátulas de juegos)
			Path rutaJuegos = Paths.get(rutaBase, "imagenes", nombre);
			if (Files.exists(rutaJuegos)) {
				sub = "imagenes";
			} else {
				// Si no está ahí, asumimos que es de la galería
				sub = "galeria_files"; // <--- NUEVA CARPETA
			}
		}

		try {
			Path ruta = Paths.get(rutaBase, sub, nombre);
			Resource r = new UrlResource(ruta.toUri());
			if (r.exists() && r.isReadable())
				return r;
		} catch (Exception e) {
		}
		return null;
	}
}