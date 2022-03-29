package de.hhu.propra.application.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

@Component
public class Praktikumskonfiguration{

  LocalDate datumStart;
  LocalDate datumEnde;
  LocalTime zeitStart;
  LocalTime zeitEnde;

  public Praktikumskonfiguration(
          @Value("${praktikum.datum.start}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumStart,
          @Value("${praktikum.datum.ende}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumEnde,
          @Value("${praktikum.zeit.start}") @DateTimeFormat(pattern = "HH:mm") LocalTime zeitStart,
          @Value("${praktikum.zeit.ende}") @DateTimeFormat(pattern = "HH:mm") LocalTime zeitEnde) {
    this.datumStart=datumStart;
    this.datumEnde=datumEnde;
    this.zeitStart=zeitStart;
    this.zeitEnde=zeitEnde;
  }

  public LocalDate getDatumStart() {
    return datumStart;
  }

  public void setDatumStart(LocalDate datumStart) {
    this.datumStart = datumStart;
  }

  public LocalDate getDatumEnde() {
    return datumEnde;
  }

  public void setDatumEnde(LocalDate datumEnde) {
    this.datumEnde = datumEnde;
  }

  public LocalTime getZeitStart() {
    return zeitStart;
  }

  public void setZeitStart(LocalTime zeitStart) {
    this.zeitStart = zeitStart;
  }

  public LocalTime getZeitEnde() {
    return zeitEnde;
  }

  public void setZeitEnde(LocalTime zeitEnde) {
    this.zeitEnde = zeitEnde;
  }
}