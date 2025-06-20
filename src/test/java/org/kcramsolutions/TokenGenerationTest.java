package org.kcramsolutions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.kcramsolutions.jcerberus.JCerberus;
import org.kcramsolutions.jcerberus.exceptions.TokenException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TokenGenerationTest {

  private JCerberus cerberus = new JCerberus("/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/public.pem", "/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/private.pem", "/home/mguzman/Projects/katalyst/agrocs-java/cerberus/certs/key.rsa");
  
  @Test
  public void create() throws JsonProcessingException, TokenException{
    String token = cerberus.createToken("Hola", "test");
    assertNotNull(token);
    assertNotEquals(token, "");
  }

  @Test
  public void verify(){
    String token = "KLUv_QBY6QIANVNrWUJoRjhtT1JaOXVWSWE5NWV5dC1iaF9OQWRxRDhraGJReWpzdHJpZGFzUS5heHhmeW81VWVJQTE1YjZOc2xZOTFRcHA3dXFndkJObkJmN09UbTBmOXl4dGZB.C_12tyEc2r4FInG31IdDSLsS6vx_90T-BkxpZrRRaaooFyg6J-EGsIf5rQ5np8T60kEZ_FGfQx9yOuEMKtUotQNh_R39iNREDvQCEJPE11enXCRc1VE1D-T0M0c6w4_cVjgOcJBAfyiFXEw1j83ZadpojaKQ-GTDig8DP5elZpoYAAHA3AcgOhSh1Md0xEVdxpjmG6PvMEL9jgEzic8C7rQY9Dgps0ji7HzdjhR3xUML7-wx42LGK-iNKx9n5kIbpfDgrekJqGTbVgBbv8jAj35QC8oqjaQiBYF6piGNKy_g_LLjx66D2P40cnkdKgy3vWmbIDkSfaAtMNoU32zijQ";
    Boolean result = cerberus.verifySign(token);
    assertEquals(result, true);
  }

  @Test
  public void extractTest() throws JsonMappingException, JsonProcessingException, TokenException{
    String token = "KLUv_QBY6QIANVNrWUJoRjhtT1JaOXVWSWE5NWV5dC1iaF9OQWRxRDhraGJReWpzdHJpZGFzUS5heHhmeW81VWVJQTE1YjZOc2xZOTFRcHA3dXFndkJObkJmN09UbTBmOXl4dGZB.C_12tyEc2r4FInG31IdDSLsS6vx_90T-BkxpZrRRaaooFyg6J-EGsIf5rQ5np8T60kEZ_FGfQx9yOuEMKtUotQNh_R39iNREDvQCEJPE11enXCRc1VE1D-T0M0c6w4_cVjgOcJBAfyiFXEw1j83ZadpojaKQ-GTDig8DP5elZpoYAAHA3AcgOhSh1Md0xEVdxpjmG6PvMEL9jgEzic8C7rQY9Dgps0ji7HzdjhR3xUML7-wx42LGK-iNKx9n5kIbpfDgrekJqGTbVgBbv8jAj35QC8oqjaQiBYF6piGNKy_g_LLjx66D2P40cnkdKgy3vWmbIDkSfaAtMNoU32zijQ";
    Object result = cerberus.extractDataObject(token, Object.class);
    assertEquals("Hola", result);
    assertEquals("test", result);
  }

}
