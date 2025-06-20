package org.kcramsolutions.jcerberus;

import org.kcramsolutions.jcerberus.exceptions.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JCerberus {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final TokenLib lib = new TokenLib();
  private static final Logger logger = LoggerFactory.getLogger(JCerberus.class);

  private String publicKeyPath;
  private String privateKeyPath;
  private String symmestricKeyPath;


  public JCerberus(String pubKeyPath, String privKeyPath, String symmestricKeyPath) {
    this.privateKeyPath = privKeyPath;
    this.publicKeyPath = pubKeyPath;
    this.symmestricKeyPath = symmestricKeyPath;
    mapper.registerModule(new JavaTimeModule());
  }
  

  public <T> T extractDataObject(String token, Class<T> clazz) throws JsonMappingException, JsonProcessingException, TokenException {
    logger.debug("init extraction");
    String jsonString = lib.extractTokenData(token, privateKeyPath, symmestricKeyPath);
    try{

      T out = mapper.readValue(jsonString, clazz);
      return out;
    }catch (Throwable e) {
      logger.error("Error mapping class", e);
      return null;
    }
  }


  public String createToken(Object headers, Object body) throws JsonProcessingException, TokenException {
    logger.debug("Header: {}", headers);
    logger.debug("body: {}", body);
    String jsonHeader = mapper.writeValueAsString(headers);
    String jsonBody = mapper.writeValueAsString(body);
    logger.debug("Header: {}", jsonHeader);
    logger.debug("body: {}", jsonBody);
    logger.debug("private: {}", privateKeyPath);
    logger.debug("Key: {}", symmestricKeyPath);
    return lib.createToken(jsonHeader, jsonBody, privateKeyPath, symmestricKeyPath);
  }

  public boolean verifySign(String token) throws TokenException {
    return lib.verify(token, publicKeyPath);
  }
}
