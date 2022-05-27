package min.project.users.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateJWT(String email, String userName, String role, long ttlMillis);

    Claims getClaims(String jwt);

    String getUserName(Claims claims);

    String getEmail(Claims claims);

    String getRole(Claims claims);

}
