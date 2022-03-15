package de.hhu.propra.database.entities;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record KlausurEntity(@Id Long id, String name, LocalDateTime datum, int dauer, long lsf, boolean online) {
}
