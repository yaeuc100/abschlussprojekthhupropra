package de.hhu.propra.application.dto;

import de.hhu.propra.domain.aggregates.student.Urlaub;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

public record UrlaubDto(
    @NotBlank @DateTimeFormat(pattern = "dd.mm.yyyy") String datum,
    @NotBlank @DateTimeFormat(pattern = "hh.mm") String startzeit,
    @NotBlank @DateTimeFormat(pattern = "hh.mm") String endzeit) {

  public static Urlaub toUrlaub(UrlaubDto dto) {
    try {
      return new Urlaub(LocalDate.parse(dto.datum),
          LocalTime.parse(dto.startzeit()),
          LocalTime.parse(dto.endzeit()));
    } catch (Exception e) {
      return null;
    }
  }

  public static UrlaubDto toUrlaubDto(Urlaub urlaub) {
    return new UrlaubDto(
        urlaub.datum().toString(),
        urlaub.startzeit().toString(),
        urlaub.endzeit().toString()
    );
  }

  @Override
  public String toString() {
    return "Urlaub{"
        + "datum=" + datum
        + ", startzeit=" + startzeit
        + ", endzeit=" + endzeit
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UrlaubDto urlaub = (UrlaubDto) o;
    return datum.equals(urlaub.datum()) && startzeit.equals(urlaub.startzeit) && endzeit
        .equals(urlaub.endzeit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datum, startzeit, endzeit);
  }
}
