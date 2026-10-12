package de.heinzdanner.asterixapi.controller;

import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/asterix")
public class AsterixController {

    private final CharacterRepository repository;

    public AsterixController(CharacterRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/characters")
    public List<Character> getCharacters() {
        return repository.findAll();
    }
}