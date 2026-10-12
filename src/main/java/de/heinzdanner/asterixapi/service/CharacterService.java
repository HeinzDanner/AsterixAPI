package de.heinzdanner.asterixapi.service;

import de.heinzdanner.asterixapi.dto.CharacterDto;
import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {

    private final CharacterRepository repository;
    private final IdService idService;

    public CharacterService(CharacterRepository repository, IdService idService) {
        this.repository = repository;
        this.idService = idService;
    }

    public List<Character> findAll(Integer age) {
        if (age == null) {
            return repository.findAll();
        }
        return repository.findByAgeLessThanEqual(age);
    }
    /*
   public List<Character> findAll(Integer age) {
    List<Character> all = repository.findAll();

    if (age == null) {
        return all;
    }

    return all.stream()
            .filter(c -> c.age() <= age)
            .toList();
}
    */


    public Optional<Character> findById(String id) {
        return repository.findById(id);
    }

    public Character create(CharacterDto dto) {
        Character character = new Character(
                idService.randomId(),
                dto.name(),
                dto.age(),
                dto.profession()
        );
        return repository.save(character);
    }

    public Optional<Character> update(String id, CharacterDto dto) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }

        Character character = new Character(id, dto.name(), dto.age(), dto.profession());
        return Optional.of(repository.save(character));
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    public Optional<Double> averageAge(String profession) {
        return Optional.ofNullable(repository.findAverageAgeByProfession(profession));
    }

    public List<Character> findOlderThan(int age) {
        return repository.findByAgeGreaterThan(age);
    }
    
}