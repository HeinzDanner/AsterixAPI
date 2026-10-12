package de.heinzdanner.asterixapi.controller;

import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/asterix/characters")
public class AsterixController {

    private final CharacterRepository repository;

    public AsterixController(CharacterRepository repository) {
        this.repository = repository;
    }

    // GET /asterix/characters  (optional: ?name=...&age=...&profession=...)
    @GetMapping
    public List<Character> getCharacters(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer age,
                                         @RequestParam(required = false) String profession) {
        return repository.findAll().stream()
                .filter(c -> name == null || c.name().equalsIgnoreCase(name))
                .filter(c -> age == null || c.age() == age)
                .filter(c -> profession == null || c.profession().equalsIgnoreCase(profession))
                .toList();
    }

    // GET /asterix/characters/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Character> getById(@PathVariable String id) {
        Optional<Character> character = repository.findById(id);

        if (character.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(character.get());
    }

    // POST /asterix/characters
    @PostMapping
    public ResponseEntity<Character> create(@RequestBody Character character) {
        Character saved = repository.save(character);
        URI location = URI.create("/asterix/characters/" + saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    // PUT /asterix/characters/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Character> update(@PathVariable String id, @RequestBody Character character) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Character toSave = new Character(id, character.name(), character.age(), character.profession());
        return ResponseEntity.ok(repository.save(toSave));
    }

    // DELETE /asterix/characters/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /asterix/characters/average-age?profession=Chief
    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge(@RequestParam String profession) {
        Double average = repository.findAverageAgeByProfession(profession);

        if (average == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(average);
    }
}