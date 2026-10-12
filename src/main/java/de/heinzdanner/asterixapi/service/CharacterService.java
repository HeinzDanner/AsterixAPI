package de.heinzdanner.asterixapi.service;

import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository repository;

    public CharacterService(CharacterRepository repository) {
        this.repository = repository;
    }

    public List<Character> findAll(String name, Integer age, String profession) {
        return repository.findAll().stream()
                .filter(c -> name == null || c.name().equalsIgnoreCase(name))
                .filter(c -> age == null || c.age() == age)
                .filter(c -> profession == null || c.profession().equalsIgnoreCase(profession))
                .toList();
    }

    public Optional<Character> findById(String id) {
        return repository.findById(id);
    }

    public Character create(Character character) {
        return repository.save(character);
    }

    public Optional<Character> update(String id, Character character) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }

        Character toSave = new Character(id, character.name(), character.age(), character.profession());
        return Optional.of(repository.save(toSave));
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    public Optional<Double> averageAge(String profession) {
        Double average = repository.findAverageAgeByProfession(profession);
        return Optional.ofNullable(average);
    }
}