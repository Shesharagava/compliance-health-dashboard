package com.internship.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.Compliance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ComplianceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ GET TEST
    @Test
    void testGetAllCompliance() throws Exception {
        mockMvc.perform(get("/api/compliance?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ✅ CREATE TEST
    @Test
    void testCreateCompliance() throws Exception {

        Compliance compliance = new Compliance();
        compliance.setTitle("Test Compliance");
        compliance.setStatus("PENDING");

        mockMvc.perform(post("/api/compliance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compliance)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Compliance"));
    }

    // ✅ UPDATE TEST (FIXED)
    @Test
    void testUpdateCompliance() throws Exception {

        // STEP 1: CREATE DATA FIRST
        Compliance compliance = new Compliance();
        compliance.setTitle("Original");
        compliance.setStatus("PENDING");

        String response = mockMvc.perform(post("/api/compliance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compliance)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Compliance created = objectMapper.readValue(response, Compliance.class);
        Long id = created.getId();

        // STEP 2: UPDATE
        compliance.setTitle("Updated Test");

        mockMvc.perform(put("/api/compliance/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compliance)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test"));
    }

    // ✅ DELETE TEST (FIXED)
    @Test
    void testDeleteCompliance() throws Exception {

        // CREATE FIRST
        Compliance compliance = new Compliance();
        compliance.setTitle("Delete Test");
        compliance.setStatus("PENDING");

        String response = mockMvc.perform(post("/api/compliance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compliance)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Compliance created = objectMapper.readValue(response, Compliance.class);
        Long id = created.getId();

        // DELETE
        mockMvc.perform(delete("/api/compliance/" + id))
                .andExpect(status().isNoContent());
    }

    // ✅ SEARCH TEST
    @Test
    void testSearchCompliance() throws Exception {
        mockMvc.perform(get("/api/compliance/search?q=Audit"))
                .andExpect(status().isOk());
    }

    // ✅ EXPORT CSV TEST
    @Test
    void testExportCSV() throws Exception {
        mockMvc.perform(get("/api/compliance/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ID,Title")));
    }
}