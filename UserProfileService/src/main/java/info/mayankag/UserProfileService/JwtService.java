package info.mayankag.UserProfileService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * This method is used to extract username from JWT token
     * @param token JWT Token from which username is extracted
     * @return Extracted username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * This method is used to generate JWT token for a user
     * @param extraClaims Claims that are to be added to JWT
     * @param userDetails User details such as username to be added to JWT
     * @return JWT Token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * This method is used to check whether the JWT Token is valid or not
     * @param token JWT Token to be checked
     * @param userDetails User details to check JWT token against
     * @return Result of JWT Token check
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !(isTokenExpired(token)));
    }

    /**
     * This method is used to check whether the JWT token has expired or not
     * @param token JWT Token for which the expiration is checked
     * @return Status of the JWT Token expiration check
     */
    public boolean isTokenExpired(String token) {
        return extractTokenExpiration(token).before(new Date());
    }

    /**
     * This method returns the expiration date of the JWT token
     * @param token JWT token for which the expiration is extracted
     * @return Date of the JWT Token Expiration
     */
    private Date extractTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * This method is used to extract individual claim from JWT Token
     * @param token JWT Token from which the claim is extracted
     * @param claimsResolver Function to get the claims from JWT Token
     * @return Value of the Claim
     * @param <T> Type of the Claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * This method is used to extract all the claims from the JWT Token
     * @param token JWT Token from which all the claims are extracted
     * @return Claims which are extracted from JWT
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJwt(token)
                .getBody();
    }

    /**
     * This method is used to get the signin key from the String Secret Key
     * @return Returns the Key which is used to Sign JWT Token
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
