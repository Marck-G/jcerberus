package org.kcramsolutions.jcerberus.jwt;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

import org.kcramsolutions.jcerberus.JCerberus;
import org.kcramsolutions.jcerberus.exceptions.InvalidTokenException;
import org.kcramsolutions.jcerberus.exceptions.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Standar {
  private static final Logger logger = LoggerFactory.getLogger(Standar.class);
  private JCerberus cerberus;
  private Headers headers;
  private Object body;

  public Standar(JCerberus cerberus) {
    this.cerberus = cerberus;
    headers = new Headers();
  }

  public Headers getHeaders() {
    return headers;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  public String create() throws JsonProcessingException, TokenException {
    if (headers == null)
      logger.warn("Haders is null");
    try {

      String token = cerberus.createToken(headers, body);
      logger.debug("New Token: {}", token);
      return token;
    } catch (Throwable e) { // Catch Throwable to be broad, or specific Exception if you throw one.
      logger.error("Error calling native createToken: " + e.getMessage());
      e.printStackTrace();
      throw new TokenException("Can't create the token");
    }
  }

  public boolean verify(String token) throws JsonMappingException, JsonProcessingException, TokenException {
    if (!cerberus.verifySign(token)) {
      return false;
    }
    Token data = cerberus.extractDataObject(token, Token.class);
    body = data.payload;
    headers = data.getHeader();
    if (!headers.expires.isAfter(Instant.now())) {
      logger.warn("Expired Token: {}", headers.getExpires());
      return false;
    }
    return true;
  }

  public void setExpiration(long durantion) {
    if (headers == null) {
      headers = new Headers(durantion);
    } else {
      headers.setExpiration(durantion);
    }
  }

  public void setExpiration(Instant durantion) {
    headers.setExpiration(durantion);
  }

  public Token extractAndVerify(String token)
      throws JsonMappingException, JsonProcessingException, TokenException {
    if (!verify(token)) {
      logger.warn("Invalid token");
      throw new InvalidTokenException("Invalid Token");
    }
    return new Token(headers, body);
  }

  public static class Token implements Serializable {
    private Headers header;
    private Object payload;

    public Token(Headers headers, Object payload) {
      this.header = headers;
      this.payload = payload;
    }
    public Token() {
      this.header = new Headers();
      this.payload = new Object();
    }

    public Headers getHeader() {
      return header;
    }

    public Object getPayload() {
      return payload;
    }
  }

  public static class Headers implements Serializable {
    private Instant expires = Instant.now().plusSeconds(24 * 3600);
    private String vendored = "jcerberus";
    private HashMap<String, Object> system = new HashMap<>();

    public Headers() {
    }

    public Headers(long ttlInSeconds) {
      this.expires = Instant.now().plusSeconds(ttlInSeconds);
    }

    public Headers(Instant expire) {
      this.expires = expire;
    }

    @Override
    public String toString() {
      return new StringBuilder("Heaser ")
          .append("expires = ").append(expires.toString())
          .append(" vendored = ").append(vendored)
          .append(" headers = ").append(system.toString())
          .toString();
    }

    private void setExpiration(long ttl) {
      this.expires = Instant.now().plusSeconds(ttl);
    }

    private void setExpiration(Instant expiration) {
      this.expires = expiration;
    }

    public void setHeader(String key, Object value) {
      system.put(key, value);
    }

    public Instant getExpires() {
      return expires;
    }

    public Object getHeader(String key) {
      return system.get(key);
    }

    public String getVendored() {
      return vendored;
    }

  }
}
