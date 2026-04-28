package com.empresa.crm_system.controller.api;

import com.empresa.crm_system.Corretor;
import com.empresa.crm_system.security.CorretorDetails;
import com.empresa.crm_system.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

            Corretor corretor = ((CorretorDetails) authentication.getPrincipal()).getCorretor();
            String token = jwtService.generateToken(
                    (CorretorDetails) authentication.getPrincipal(),
                    Map.of("corretorId", corretor.getId(), "nome", corretor.getNome()));

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token", token);
            response.put("corretor", corretor);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou senha invalidos"));
        }
    }

    public record LoginRequest(String email, String senha) {}
}
