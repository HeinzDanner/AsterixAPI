package de.heinzdanner.asterixapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


public record Character(@Id String id, String name, int age, String profession) {
}
