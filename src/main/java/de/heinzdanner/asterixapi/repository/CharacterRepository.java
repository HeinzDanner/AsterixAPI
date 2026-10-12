package de.heinzdanner.asterixapi.repository;

import de.heinzdanner.asterixapi.model.Character;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterRepository extends MongoRepository<Character, String> {
}
