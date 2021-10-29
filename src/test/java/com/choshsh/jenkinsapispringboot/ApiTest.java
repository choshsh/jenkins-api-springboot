package com.choshsh.jenkinsapispringboot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles({"dev"})
@AutoConfigureMockMvc
public class ApiTest {

  @Autowired
  protected MockMvc mockMvc;

  @Test
  public void GET_GITHUB_FILE() throws Exception {
    mockMvc.perform(get("/jenkins/pyscript"))
        .andExpect(status().isOk())
        .andDo(print());
  }

}
