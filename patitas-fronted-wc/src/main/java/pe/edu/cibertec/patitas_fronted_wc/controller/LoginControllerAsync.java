package pe.edu.cibertec.patitas_fronted_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_fronted_wc.clientes.AutenticacionClientes;
import pe.edu.cibertec.patitas_fronted_wc.dto.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;

    @Autowired
    AutenticacionClientes autenticacionClientes;

    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {
        // Validar campos de entrada
        if (loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().isEmpty() ||
                loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().isEmpty() ||
                loginRequestDTO.password() == null || loginRequestDTO.password().trim().isEmpty()) {
            return Mono.just(new LoginResponseDTO("01", "Error: Debe completar correctamente sus credenciales", "", "", "", ""));
        }

        try {
            // Consumir servicio de autenticación (Del Backend)
            return webClientAutenticacion.post()
                    .uri("/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {
                        if (response.codigo().equals("00")) {
                            return Mono.just(new LoginResponseDTO("00", "", response.nombreUsuario(), response.correoUsuario(), response.tdoc(), response.ndoc()));
                        } else {
                            return Mono.just(new LoginResponseDTO("02", "Error: Autenticación fallida", "", "", "", ""));
                        }
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "Error: Ocurrió un problema en la autenticación", "", "", "", ""));
        }
    }

    @PostMapping("/logout-async")
    public Mono<LogoutResponseDTO> cerrarSesion(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        try {
            return webClientAutenticacion.post()
                    .uri("/logout")
                    .body(Mono.just(logoutRequestDTO), LogoutRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LogoutResponseDTO.class)
                    .flatMap(response -> {
                        if (response.codigo().equals("00")) {
                            return Mono.just(new LogoutResponseDTO("00", "Cierre de sesión exitoso"));
                        } else {
                            return Mono.just(new LogoutResponseDTO("02", "Error al cerrar sesión"));
                        }
                    });
        } catch (Exception e) {
            return Mono.just(new LogoutResponseDTO("99", "Error en el proceso de cierre de sesión"));
        }
    }

    @PostMapping("/logout_ef")
    public LogoutResponseEFDTO cerrarSesionFeign(@RequestBody LogoutRequestEFDTO request) {
        try {
            // Consumimos servicio con Feign Client
            ResponseEntity<LogoutResponseEFDTO> response = autenticacionClientes.logoutEF(request);
            System.out.println("Cerrando sesión EXAMEN FINAL");
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return new LogoutResponseEFDTO("99", "Ocurrió un problema");
            }
        } catch (Exception e) {
            return new LogoutResponseEFDTO("99", e.getMessage());
        }
    }
}