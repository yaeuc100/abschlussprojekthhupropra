package de.hhu.propra.database.entities;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record AuditEntity(@Id Long id , String aenderung, String handle, LocalDateTime zeitpunkt) {
}
