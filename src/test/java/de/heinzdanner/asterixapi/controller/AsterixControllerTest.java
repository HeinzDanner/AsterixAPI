package de.heinzdanner.asterixapi.controller;

import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import de.heinzdanner.asterixapi.model.Character;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest
@AutoConfigureMockMvc
class AsterixControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharacterRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void getAllCharacters_returnsSavedCharacter() throws Exception {
        // 1. Testdaten direkt über das Repository speichern (nicht über POST)
        repository.save(new Character("1", "Asterix", 35, "Warrior"));

        // 2. Anfrage simulieren und prüfen
        mockMvc.perform(MockMvcRequestBuilders.get("/asterix/characters"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                          {
                            "id": "1",
                            "name": "Asterix",
                            "age": 35,
                            "profession": "Warrior"
                          }
                        ]
                        """));
    }

    @Test
    void getById_existingId_returnsCharacter() throws Exception {
        // 1. Charakter in der Testdatenbank speichern
        repository.save(new Character("1", "Asterix", 35, "Warrior"));

        // 2. GET-Anfrage für ID "1" ausführen und JSON-Inhalt prüfen
        mockMvc.perform(MockMvcRequestBuilders.get("/asterix/characters/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "id": "1",
                          "name": "Asterix",
                          "age": 35,
                          "profession": "Warrior"
                        }
                        """));
    }

    @Test
    void getById_unknownId_returnsNotFound() throws Exception {
        // GET-Anfrage für eine nicht existierende ID ausführen
        mockMvc.perform(MockMvcRequestBuilders.get("/asterix/characters/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void create_savesAndReturnsCharacterWithGeneratedId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/asterix/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Obelix",
                                  "age": 35,
                                  "profession": "Supplier"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "name": "Obelix",
                          "age": 35,
                          "profession": "Supplier"
                        }
                        """));

        assertEquals(1, repository.count());
    }

    @Test
    void update_existingId_returnsUpdatedCharacter() throws Exception {
        repository.save(new Character("1", "Asterix", 35, "Warrior"));

        mockMvc.perform(MockMvcRequestBuilders.put("/asterix/characters/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Asterix",
                                  "age": 36,
                                  "profession": "Warrior"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "id": "1",
                          "name": "Asterix",
                          "age": 36,
                          "profession": "Warrior"
                        }
                        """));

        assertEquals(36, repository.findById("1").get().age());
    }

    @Test
    void update_unknownId_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/asterix/characters/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nobody",
                                  "age": 1,
                                  "profession": "None"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(0, repository.count());
    }

    @Test
    void delete_existingId_returnsNoContent() throws Exception {
        repository.save(new Character("1", "Asterix", 35, "Warrior"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/asterix/characters/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertFalse(repository.existsById("1"));
    }

    @Test
    void delete_unknownId_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/asterix/characters/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAverageAge_knownProfession_returnsAverage() throws Exception {
        repository.save(new Character("3", "Miraculix", 60, "Druid"));
        repository.save(new Character("4", "Majestix", 60, "Chief"));
        repository.save(new Character("11", "Vitalstatistix", 50, "Chief"));

        mockMvc.perform(MockMvcRequestBuilders.get("/asterix/characters/average-age")
                        .param("profession", "Chief"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("55.0"));
    }

    @Test
    void getAverageAge_unknownProfession_returnsNotFound() throws Exception {
        repository.save(new Character("1", "Asterix", 35, "Warrior"));

        mockMvc.perform(MockMvcRequestBuilders.get("/asterix/characters/average-age")
                        .param("profession", "Pirate"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}