package model.utils.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.DTO.MessageDTO;
import model.entity.DTO.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private String INVALID_TOKEN_ERROR_MSG = "Токен недействителен";

    private JwtUtil jwtUtil = new JwtUtil();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtUtil.validate(token);

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                System.out.println(role);

                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(claims.get("userId", Integer.class), claims.getSubject()),
                                null,
                                authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println(SecurityContextHolder.getContext().getAuthentication());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new MessageDTO("UNAUTHORIZED", INVALID_TOKEN_ERROR_MSG).toString());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}