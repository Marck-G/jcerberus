package org.kcramsolutions.jcerberus.jwt;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

import org.kcramsolutions.jcerberus.JCerberus;
import org.kcramsolutions.jcerberus.exceptions.InvalidTokenException;
import org.kcramsolutions.jcerberus.exceptions.TokenException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Standar<BODY> {
    private JCerberus cerberus;
    private Headers headers;
    private BODY body;

    public Standar(JCerberus cerberus){
      this.cerberus = cerberus;
    }

    public Headers getHeaders(){
      return headers;
    }

    public void setBody(BODY body) {
      this.body = body;
    }

    public String create() throws JsonProcessingException, TokenException {
      return cerberus.createToke(headers, body);
    }

    public boolean verify(String token) throws JsonMappingException, JsonProcessingException, TokenException {
      if (!cerberus.verifySign(token)) {
        return false;
      }
      Token<BODY> tmp = new Token<BODY>(headers, body);
      Token<BODY> data = cerberus.extractData(token, tmp.getClass());
      body = data.payload;
      headers = data.getHeader();
      if (!headers.expires.isBefore(Instant.now())) {
        return false;
      }
      return true;
    }

    public Token<BODY> extractAndVerify(String token) throws JsonMappingException, JsonProcessingException, TokenException {
       if (!verify(token)) {
        throw new InvalidTokenException("Invalid Token");
       }
       return new Token<BODY>(headers, body);
    }

    public static class Token<T> implements Serializable{
      private Headers header;
      private  T payload;

      public Token(Headers headers, T payload) {
        this.header = headers;
        this.payload = payload;
      }

      public Headers getHeader() {
        return header;
      }
      public T getPayload() {
        return payload;
      }
    }

    public static class Headers  implements Serializable{
      private Instant expires = Instant.now().plusSeconds(24*3600);
      private String vendored = "jcerberus";
      private HashMap<String, Object> system = new HashMap<>();

      public Headers(){}
      public Headers(long ttlInSeconds){
        this.expires = Instant.now().plusSeconds(ttlInSeconds);
      }
      public Headers(Instant expire){
        this.expires = expire;
      }
      
      public void setHeader(String key, Object value){
        system.put(key, value);
      }

      public Instant geExpires() {
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
