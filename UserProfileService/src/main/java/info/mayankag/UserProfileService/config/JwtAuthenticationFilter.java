package info.mayankag.UserProfileService.config;

import info.mayankag.UserProfileService.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

   protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
      String authorizationHeader = request.getHeader("Authorization");
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
         String jwt = authorizationHeader.substring(7);
         this.jwtService.extractUsername(jwt);
      } else {
         filterChain.doFilter(request, response);
      }
   }
}
