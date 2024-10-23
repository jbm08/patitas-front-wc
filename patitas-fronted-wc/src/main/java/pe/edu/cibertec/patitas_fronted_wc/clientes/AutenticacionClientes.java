package pe.edu.cibertec.patitas_fronted_wc.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.cibertec.patitas_fronted_wc.dto.LogoutRequestEFDTO;
import pe.edu.cibertec.patitas_fronted_wc.dto.LogoutResponseEFDTO;


@FeignClient(name = "autenticacion", url = "http://localhost:8081/autenticacion")
public interface AutenticacionClientes {
  @PostMapping("/logout_ef")
  ResponseEntity<LogoutResponseEFDTO> logoutEF(@RequestBody LogoutRequestEFDTO request);
}