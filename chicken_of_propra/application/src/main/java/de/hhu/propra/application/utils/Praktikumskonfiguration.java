package de.hhu.propra.application.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Praktikumskonfiguration{

  LocalDate datumStart;
  LocalDate datumEnde;
  LocalTime zeitStart;
  LocalTime zeitEnde;
/*
  public Praktikumskonfiguration(@Value("#{T(java.time.LocalDate).parse('${praktikum.datum.start}')}") LocalDate datumStart,
      @Value("#{T(java.time.LocalDate).parse('${praktikum.datum.ende}')}") LocalDate datumEnde,
      @Value("#{T(java.time.LocalTime).parse('${praktikum.zeit.start}')}") LocalTime zeitStart,
      @Value("#{T(java.time.LocalTime).parse('${praktikum.zeit.ende}')}") LocalTime zeitEnde){
    this.datumStart=datumStart;
    this.datumEnde=datumEnde;
    this.zeitStart=zeitStart;
    this.zeitEnde=zeitEnde;
  }
*/
/*
@Bean
public Praktikumskonfiguration konfiguration(@Value("#{T(java.time.LocalDate).parse('${praktikum.datum.start}')}") LocalDate datumStart,
    @Value("#{T(java.time.LocalDate).parse('${praktikum.datum.ende}')}") LocalDate datumEnde,
    @Value("#{T(java.time.LocalTime).parse('${praktikum.zeit.start}')}") LocalTime zeitStart,
    @Value("#{T(java.time.LocalTime).parse('${praktikum.zeit.ende}')}") LocalTime zeitEnde){
  return new Praktikumskonfiguration();
}
*/


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