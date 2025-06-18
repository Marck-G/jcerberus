package org.kcramsolutions.jcerberus;

import org.kcramsolutions.jcerberus.exceptions.TokenException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JCerberus {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final TokenLib lib = new TokenLib();

  private String publicKeyPath;
  private String privateKeyPath;
  private String symmestricKeyPath;


  private JCerberus(String pubKeyPath, String privKeyPath, String symmestricKeyPath) {
    this.privateKeyPath = privKeyPath;
    this.publicKeyPath = pubKeyPath;
    this.symmestricKeyPath = symmestricKeyPath;
  }
  

  public <T> T extractData(String token, Class<T> clazz) throws JsonMappingException, JsonProcessingException, TokenException {
    String jsonString = lib.extractTokenData(token, privateKeyPath, symmestricKeyPath);
    return mapper.readValue(jsonString, clazz);
  }


  public String createToke(Object headers, Object body) throws JsonProcessingException, TokenException {
    String jsonHeader = mapper.writeValueAsString(headers);
    String jsonBody = mapper.writeValueAsString(body);
    return lib.createToken(jsonHeader, jsonBody, privateKeyPath, symmestricKeyPath);
  }

  public boolean verifySign(String token) throws TokenException {
    return lib.verify(token, publicKeyPath);
  }
}
