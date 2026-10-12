package de.heinzdanner.asterixapi.repository;

import de.heinzdanner.asterixapi.model.Character;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharacterRepository extends MongoRepository<Character, String> {

    @Aggregation(pipeline = {
            "{ $match: { profession: ?0 } }",
            "{ $group: { _id: null, avgAge: { $avg: '$age' } } }"
    })
    Double findAverageAgeByProfession(String profession);
}