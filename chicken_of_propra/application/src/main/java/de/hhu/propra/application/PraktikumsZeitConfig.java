package de.hhu.propra.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


public class PraktikumsZeitConfig {

  private final LocalTime praktikumStart;
  private final LocalTime praktikumEnde;


  public PraktikumsZeitConfig(
      @Value("${praktikum.start}") String praktikumStart,
      @Value("${praktikum.end}") String praktikumEnde) {
    this.praktikumStart = LocalTime.parse(praktikumStart);
    this.praktikumEnde = LocalTime.parse(praktikumEnde);
  }

  @Bean
  public LocalTime praktikumStart() {
    return praktikumStart;
  }

  @Bean
  public LocalTime praktikumEnde() {
    return praktikumEnde;
  }

}