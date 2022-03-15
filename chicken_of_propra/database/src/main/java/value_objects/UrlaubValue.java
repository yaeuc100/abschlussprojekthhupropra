package value_objects;

import java.time.LocalDate;
import java.time.LocalTime;

public record UrlaubValue (LocalDate datum, LocalTime startzeit, LocalTime endzeit) {
}
