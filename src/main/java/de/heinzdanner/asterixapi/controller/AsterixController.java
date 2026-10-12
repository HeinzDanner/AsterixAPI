package de.heinzdanner.asterixapi.controller;

import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.service.CharacterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/asterix/characters")
public class AsterixController {

    private final CharacterService service;

    public AsterixController(CharacterService service) {
        this.service = service;
    }

    @GetMapping
    public List<Character> getCharacters(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer age,
                                         @RequestParam(required = false) String profession) {
        return service.findAll(name, age, profession);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getById(@PathVariable String id) {
        Optional<Character> character = service.findById(id);

        if (character.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(character.get());
    }

    // Charakter erstellen (CRUD-Aufgabe)
    @PostMapping
    public ResponseEntity<Character> create(@RequestBody Character character) {
        Character saved = service.create(character);
        URI location = URI.create("/asterix/characters/" + saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> update(@PathVariable String id, @RequestBody Character character) {
        Optional<Character> updated = service.update(id, character);

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated.get());
        //   return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge(@RequestParam String profession) {
        Optional<Double> average = service.averageAge(profession);

        if (average.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(average.get());
    }
}