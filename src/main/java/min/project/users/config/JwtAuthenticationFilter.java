package min.project.users.config;

import io.jsonwebtoken.Claims;
import min.project.users.data.dto.UserPrinciple;
import min.project.users.service.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String AUTH_HEADER_VALUE_PREFIX = "Bearer ";
    private static final Logger LOG = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            String jwt = getBearerToken(httpRequest);
            if (jwt != null && !jwt.isEmpty()) {
                Claims jwtClaims = jwtService.getClaims(jwt);
                String email = jwtService.getEmail(jwtClaims);
                String userName = jwtService.getUserName(jwtClaims);
                String role = jwtService.getRole(jwtClaims);

                Collection<SimpleGrantedAuthority> authorities = null;
                if (StringUtils.isNotBlank(role)) {
                    authorities = Arrays.stream(role.split(",")).map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
                System.out.println(authorities.size());
                UserPrinciple userPrinciple = new UserPrinciple(email, userName, authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrinciple, null,
                        authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            LOG.severe("Failed Security token : " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.info("JwtAuthenticationFilter destroyed");
    }

    private String getBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER_KEY);
        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_VALUE_PREFIX)) {
            return authHeader.substring(AUTH_HEADER_VALUE_PREFIX.length());
        }
        return null;
    }
}
