package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import de.hhu.propra.domain.auditlog.AuditLog;

public class AuditLogErzeugung {

  public static AuditLog urlaubHinzugefuegt(String studentHandle, Urlaub urlaub) {
    String aenderung = urlaub.toString() + " hinzugefuegt";
    return new AuditLog(aenderung, studentHandle);
  }

  public static AuditLog urlaubStorniert(String studentHandle, UrlaubDto urlaub) {
    String aenderung = urlaub.toString() + " storniert";
    return new AuditLog(aenderung, studentHandle);
  }

  public static AuditLog klausurErstellen(String studentHandle, Klausur klausur) {
    String aenderung = "Klausur{id="
        + klausur.id().toString()
        + ", name="
        + klausur.name()
        + "} erstellt";
    return new AuditLog(aenderung, studentHandle);
  }

  public static AuditLog klausurAnmelden(String studentHandle, Klausur klausur) {
    String aenderung = "Klausur{id="
        + klausur.id().toString()
        + ", name="
        + klausur.name()
        + "} angemeldet";
    return new AuditLog(aenderung, studentHandle);
  }

  public static AuditLog klausurStorniert(String studentHandle, Klausur klausur) {
    String aenderung = "Klausur{id="
        + klausur.id().toString()
        + ", name="
        + klausur.name()
        + "} storniert";
    return new AuditLog(aenderung, studentHandle);
  }
}
