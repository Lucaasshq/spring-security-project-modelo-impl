/*
 * Copyright (c) 2026 ChegaAI. Todos os direitos reservados.
 * Este software é proprietário da ChegaAI.
 * A cópia, distribuição ou uso não autorizado é estritamente proibido.
 */

package br.com.modelo.modelo_backend.security;


import br.com.modelo.modelo_backend.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;
    private final long expirationTime;

    /**
     * Construtor explícito que injeta as propriedades de configuração do JWT
     * (mapeadas para ler o application.properties e as variáveis de ambiente).
     */

    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expire-days}") long expireDays,
                      @Value("${jwt.expire-hours}") long expireHours,
                      @Value("${jwt.expire-minutes}") long expireMinutes
    ){
        this.secretKey = secretKey;

        // Converte dias, horas e minutos para milissegundos totais para o jjwt
        long diasEmMilissegundos = expireDays * 24 * 60 * 60 * 1000;
        long horasEmMilissegundos = expireHours * 60 * 60 * 1000;
        long minutosEmMilissegundos = expireMinutes * 60 * 1000;

        this.expirationTime = diasEmMilissegundos + horasEmMilissegundos + minutosEmMilissegundos;
    }

    /**
     * Gera uma chave de assinatura HMAC segura a partir da String secreta.
     */
    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Cria um novo token JWT contendo informações do usuário (claims customizadas como restauranteId e papel).
     */

    public String generateToken(Usuario usuario){
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("papel", usuario.getPapel().name())
                .claim("papelDescricao", usuario.getPapel().getDescricao())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Função genérica para extrair qualquer claim do token.
     */
    public String extrairUsername(String token) {
        return extrairClaim(token, Claims -> Claims.getSubject());
    }


    /**
     * Função genérica para extrair qualquer claim do payload do token.
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    /**
     * Verifica se a data de expiração do token já passou.
     */
    private boolean isTokenExpirado(String token) {
        return extrairClaim(token, Claims -> Claims.getExpiration()).before(new Date());
    }

    /**
     * Verifica se a data de expiração do token já passou.
     */
    public boolean isTokenValido(String token, UserDetails usuario){
        final String username = extrairUsername(token);
        return (username.equals(usuario.getUsername()) && !isTokenExpirado(token));
    }
}
