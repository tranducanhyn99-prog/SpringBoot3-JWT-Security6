package vn.iotstar.services;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(expiryDate);

            if (extraClaims != null) {
                extraClaims.forEach(claimsBuilder::claim);
            }

            SignedJWT signedJWT = new SignedJWT(header, claimsBuilder.build());
            JWSSigner signer = new MACSigner(getSigningKeyBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT with Nimbus JOSE: " + e.getMessage(), e);
        }
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(getSigningKeyBytes());

            if (!signedJWT.verify(verifier)) {
                return false;
            }

            String username = signedJWT.getJWTClaimsSet().getSubject();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean isExpired = expirationTime != null && expirationTime.before(new Date());

            return username != null && username.equals(userDetails.getUsername()) && !isExpired;
        } catch (Exception e) {
            return false;
        }
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(getSigningKeyBytes());

            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    private byte[] getSigningKeyBytes() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return padded;
        }
        return keyBytes;
    }
}
