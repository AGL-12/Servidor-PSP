package eus.tartanga.psp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta etiqueta es OBLIGATORIA. Convierte el programa en un Servidor Web.
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        // Esta línea arranca Spring Boot, busca tus Controladores y abre el puerto 8080
        SpringApplication.run(App.class, args);
        System.out.println("🚀 SERVIDOR TARTANGA FUNCIONANDO EN EL PUERTO 8080 🚀");
    }
}