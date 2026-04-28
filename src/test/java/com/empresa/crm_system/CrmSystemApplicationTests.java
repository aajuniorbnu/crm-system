package com.empresa.crm_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrmSystemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void deveListarImoveisSeedados() throws Exception {
        mockMvc.perform(get("/api/imoveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void deveExibirIndicadoresDeImoveis() throws Exception {
        mockMvc.perform(get("/api/imoveis/indicadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalImoveis").value(greaterThan(0)))
                .andExpect(jsonPath("$.imoveisVendaDisponiveis").exists())
                .andExpect(jsonPath("$.imoveisAluguelDisponiveis").exists());
    }
}
