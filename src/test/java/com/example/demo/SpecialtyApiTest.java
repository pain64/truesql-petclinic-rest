package com.example.demo;

import net.truej.sql.TrueSql;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.demo.PetClinic.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TrueSql public class SpecialtyApiTest extends Runner {

    @Autowired MockMvc mvc;
    @Autowired MainDb ds;

    @Test void all() throws Exception {
        ds.q("""
            INSERT INTO specialties VALUES (1, 'radiology') ON CONFLICT DO NOTHING;
            INSERT INTO specialties VALUES (2, 'surgery') ON CONFLICT DO NOTHING;
            INSERT INTO specialties VALUES (3, 'dentistry') ON CONFLICT DO NOTHING;
            """).fetchNone();

        mvc.perform(
            get("/specialties")
        ).andExpect(
            content().json("""
                [
                  {
                    "id": 1,
                    "name": "radiology"
                  },
                  {
                    "id": 2,
                    "name": "surgery"
                  },
                  {
                    "id": 3,
                    "name": "dentistry"
                  }
                ]"""
            )
        );
    }
}