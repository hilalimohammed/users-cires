package min.project.users.service.impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import min.project.users.service.JwtService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    private Key key;

    private final static String ROLES_CLAIM = "roles";
    private final static String EMAIL_CLAIM = "email";
    private final static String USERNAME_CLAIM = "username";

    @PostConstruct
    private void init() {
        this.key = new SecretKeySpec("test".getBytes(), SignatureAlgorithm.HS256.getValue());
    }

    @Override
    public String generateJWT(String email, String userName, String role, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setIssuedAt(now);
        if (email != null) {
            builder.claim(EMAIL_CLAIM, email);
        }
        if (userName != null) {
            builder.claim(USERNAME_CLAIM, userName);
        }
        if (role != null) {
            builder.claim(ROLES_CLAIM, role);
        }
        builder.setSubject(String.valueOf(new Date().getTime()));
        builder.signWith(SignatureAlgorithm.HS256, key);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    @Override
    public Claims getClaims(String jwt) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
    }

    @Override
    public String getEmail(Claims claims) {
        return claims.get(EMAIL_CLAIM, String.class);
    }

    @Override
    public String getRole(Claims claims) {
        return claims.get(ROLES_CLAIM, String.class);
    }

    @Override
    public String getUserName(Claims claims) {
        return claims.get(USERNAME_CLAIM, String.class);
    }
}
