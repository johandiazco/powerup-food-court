package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    @Schema(description = "Token JWT de acceso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type;

    @Schema(description = "Correo del usuario autenticado", example = "admin@plazoleta.com")
    private String correo;

    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String rol;

    @Schema(description = "Tiempo de expiración en milisegundos", example = "3600000")
    private long expiresIn;
}
