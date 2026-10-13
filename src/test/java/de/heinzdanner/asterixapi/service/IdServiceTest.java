package de.heinzdanner.asterixapi.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdServiceTest {

    private final IdService idService = new IdService();

    @Test
    void randomId_returnsMockedUuid() {
        UUID fixedUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(fixedUuid);

            String result = idService.randomId();

            assertEquals("123e4567-e89b-12d3-a456-426614174000", result);
            mockedUuid.verify(UUID::randomUUID, times(1));
        }
    }

    @Test
    void randomId_withoutMock_returnsValidUuid() {
        String result = idService.randomId();

        assertDoesNotThrow(() -> UUID.fromString(result));
        assertEquals(36, result.length());
    }

    @Test
    void randomId_twoCalls_returnDifferentIds() {
        String first = idService.randomId();
        String second = idService.randomId();

        assertNotEquals(first, second);
    }
}