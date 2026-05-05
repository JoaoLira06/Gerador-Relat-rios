package com.empresa.reportgenerator.dto.auth;

import com.empresa.reportgenerator.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AuthenticationResponse {

    private String token;
    private String username;
    private UserRole role;
}
