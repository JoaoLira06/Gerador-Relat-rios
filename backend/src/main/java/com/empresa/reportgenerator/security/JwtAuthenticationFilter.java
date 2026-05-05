package com.empresa.reportgenerator.security;

import com.empresa.reportgenerator.repository.RevokedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro de autenticação JWT.
 * Intercepta todas as requisições HTTP e valida o token JWT.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Extrai o token JWT do header Authorization
            String jwt = getJwtFromRequest(request);

            // 2. Se o token existe e é válido
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // 3. Extrai o JTI (ID único do token)
                String jti = jwtTokenProvider.getJtiFromToken(jwt);

                // 4. Verifica se o token foi revogado
                if (!revokedTokenRepository.existsByTokenJti(jti)) {

                    // 5. Extrai username e roles do token
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    String rolesString = jwtTokenProvider.getRolesFromToken(jwt);

                    // 6. Converte roles de String para List<SimpleGrantedAuthority>
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // 7. Cria objeto de autenticação do Spring Security
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 8. Configura o SecurityContext (informa ao Spring Security quem é o usuário)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Log do erro (em produção, usar logger apropriado)
            System.err.println("Erro ao processar token JWT: " + ex.getMessage());
        }

        // 9. Continua a cadeia de filtros (permite que a requisição prossiga)
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization.
     *
     * Header esperado: Authorization: Bearer eyJhbGc...
     *
     * @param request Requisição HTTP
     * @return Token JWT ou null se não existir
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Verifica se o header existe e começa com "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Remove o prefixo "Bearer " e retorna só o token
            return bearerToken.substring(7);
        }

        return null;
    }
}
