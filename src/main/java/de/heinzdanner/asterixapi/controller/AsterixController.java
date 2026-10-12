package de.heinzdanner.asterixapi.controller;

import de.heinzdanner.asterixapi.dto.CharacterDto;
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

    // GET /asterix/characters  oder  /asterix/characters?age=35  (maximales Alter)
    @GetMapping
    public List<Character> getAllCharacters(@RequestParam(required = false) Integer age,
                                            @RequestParam(required = false) Integer ageGreaterThan) {
        if (ageGreaterThan != null) {
            return service.findOlderThan(ageGreaterThan);
        }
        return service.findAll(age);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getById(@PathVariable String id) {
        Optional<Character> character = service.findById(id);

        if (character.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(character.get());
    }

    @PostMapping
    public ResponseEntity<Character> create(@RequestBody CharacterDto dto) {
        Character saved = service.create(dto);
        URI location = URI.create("/asterix/characters/" + saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> update(@PathVariable String id, @RequestBody CharacterDto dto) {
        Optional<Character> updated = service.update(id, dto);

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated.get());
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