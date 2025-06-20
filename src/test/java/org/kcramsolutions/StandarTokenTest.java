package org.kcramsolutions;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.kcramsolutions.jcerberus.JCerberus;
import org.kcramsolutions.jcerberus.exceptions.TokenException;
import org.kcramsolutions.jcerberus.jwt.Standar;
import org.kcramsolutions.jcerberus.jwt.Standar.Token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class StandarTokenTest {

  private JCerberus cerberus = new JCerberus("/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/public.pem", "/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/private.pem", "/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/key.rsa");

  @Test
  public void create() throws JsonProcessingException, TokenException {
    Standar token_data = new Standar(cerberus);
    token_data.setBody("Hola");
    token_data.setExpiration(132000);
    String out = token_data.create();
    assertNotNull(out);
  }

  @Test
  public void extratTest() throws JsonMappingException, JsonProcessingException, TokenException {
    String token = "KLUv_QBYxQQA4gklIBBL-FmwLecphLCC3qZRyWsVppvOBmoJjTerqqqqqroqGoYAJMov7FD4gjjzRgT49q8D-_S16BeqQr6sG_ILusCTh3BXA68ytqyA9Cts7xKqAJ4QiUqb8bBSfv0lbeZszPzDFQh9G4XHOtgSD7Q6ZBjuRnMiZHHnBEs6iXEM3kCxcA9803OTpZw8Tt0XBm5pLCYCrgA.M16AFS7HhVs46bzW9sRC09lFKZHFonxG2z0zzgaGydTc9aESgxluQy4GDr8Ls4aOTpZ5fqPw2DQ5rf9zmH2QbGt2Z7QStnlWEhbiYwW_9gvxXh2DzUMd7ny4JPR4hHDkOaFt-BQfieQ1fvU2Uga2ZKX5OUKfyAnEmAcyfhZw7xoE8L7yydI3eVTTVEaFwoGZbZ_rCFsJi818zvUp6vcf5CUYQLV56z_Q1oHIDLQCuKHrnmVjNk6Y_EF0o7l_QZVIPqGWrC53NA-mKnO7tq1IADdbDGv98kj3SO4NFeNm9E0OEVSK4yhGYCvLEBS6SdEzKgSY9pwQqlkTN36Fqk0i7A";
    Standar token_data = new Standar(cerberus);
    Token data = token_data.extractAndVerify(token);
    assertNotNull(data);
  }

}
