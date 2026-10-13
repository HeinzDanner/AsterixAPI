package de.heinzdanner.asterixapi.service;

import de.heinzdanner.asterixapi.dto.CharacterDto;
import de.heinzdanner.asterixapi.model.Character;
import de.heinzdanner.asterixapi.repository.CharacterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CharacterServiceTest {

    private final CharacterRepository repository = mock(CharacterRepository.class);
    private final IdService idService = mock(IdService.class);
    private final CharacterService service = new CharacterService(repository, idService);

    private final Character asterix = new Character("1", "Asterix", 35, "Warrior");
    private final Character idefix = new Character("7", "Idefix", 5, "Dog");

    @AfterEach
    void noMoreInteractions() {
        verifyNoMoreInteractions(repository, idService);
    }

    @Test
    void findAll_withoutAge_returnsAllCharacters() {
        when(repository.findAll()).thenReturn(List.of(asterix, idefix));

        List<Character> result = service.findAll(null);

        assertEquals(List.of(asterix, idefix), result);
        verify(repository, times(1)).findAll();
        //  verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_withAge_usesDerivedQuery() {
        when(repository.findByAgeLessThanEqual(10)).thenReturn(List.of(idefix));

        List<Character> result = service.findAll(10);

        assertEquals(List.of(idefix), result);
        verify(repository, times(1)).findByAgeLessThanEqual(10);
        // verifyNoMoreInteractions(repository);
    }

    @Test
    void findById_existingId_returnsCharacter() {
        when(repository.findById("1")).thenReturn(Optional.of(asterix));

        Optional<Character> result = service.findById("1");

        assertTrue(result.isPresent());
        assertEquals(asterix, result.get());
        verify(repository, times(1)).findById("1");
        //  verifyNoMoreInteractions(repository);
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        Optional<Character> result = service.findById("99");

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById("99");
        //  verifyNoMoreInteractions(repository);
    }

    @Test
    void create_usesIdFromIdService() {
        CharacterDto dto = new CharacterDto("Methusalix", 93, "Elder");
        Character expected = new Character("abc-123", "Methusalix", 93, "Elder");

        when(idService.randomId()).thenReturn("abc-123");
        when(repository.save(expected)).thenReturn(expected);

        Character result = service.create(dto);

        assertEquals(expected, result);
        verify(idService, times(1)).randomId();
        verify(repository, times(1)).save(expected);
        // verifyNoMoreInteractions(repository, idService);
    }

    @Test
    void update_existingId_savesWithIdFromPath() {
        CharacterDto dto = new CharacterDto("Asterix", 36, "Warrior");
        Character expected = new Character("1", "Asterix", 36, "Warrior");

        when(repository.existsById("1")).thenReturn(true);
        when(repository.save(expected)).thenReturn(expected);

        Optional<Character> result = service.update("1", dto);

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).save(expected);
        //  verifyNoMoreInteractions(repository);
    }

    @Test
    void update_unknownId_returnsEmptyAndDoesNotSave() {
        CharacterDto dto = new CharacterDto("Nobody", 1, "None");
        when(repository.existsById("99")).thenReturn(false);

        Optional<Character> result = service.update("99", dto);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).existsById("99");
        //   verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_existingId_returnsTrue() {
        when(repository.existsById("1")).thenReturn(true);

        boolean result = service.delete("1");

        assertTrue(result);
        verify(repository, times(1)).existsById("1");
        verify(repository, times(1)).deleteById("1");
        //   verifyNoMoreInteractions(repository);
    }

    @Test
    void delete_unknownId_returnsFalseAndDoesNotDelete() {
        when(repository.existsById("99")).thenReturn(false);

        boolean result = service.delete("99");

        assertFalse(result);
        verify(repository, times(1)).existsById("99");
        //   verifyNoMoreInteractions(repository);
    }

    @Test
    void averageAge_knownProfession_returnsValue() {
        when(repository.findAverageAgeByProfession("Chief")).thenReturn(60.0);

        Optional<Double> result = service.averageAge("Chief");

        assertTrue(result.isPresent());
        assertEquals(60.0, result.get());
        verify(repository, times(1)).findAverageAgeByProfession("Chief");
        //   verifyNoMoreInteractions(repository);
    }

    @Test
    void averageAge_unknownProfession_returnsEmpty() {
        when(repository.findAverageAgeByProfession("Pirate")).thenReturn(null);

        Optional<Double> result = service.averageAge("Pirate");

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAverageAgeByProfession("Pirate");
        //   verifyNoMoreInteractions(repository);
    }

    @Test
    void findOlderThan_usesDerivedQuery() {
        when(repository.findByAgeGreaterThan(20)).thenReturn(List.of(asterix));

        List<Character> result = service.findOlderThan(20);

        assertEquals(List.of(asterix), result);
        verify(repository, times(1)).findByAgeGreaterThan(20);
        //  verifyNoMoreInteractions(repository);
    }
}