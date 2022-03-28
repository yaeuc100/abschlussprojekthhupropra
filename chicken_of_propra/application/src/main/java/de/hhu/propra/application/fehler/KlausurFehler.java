package de.hhu.propra.application.fehler;

public class KlausurFehler {

  public static final String DATUM_FALSCH = "Das Datum ist falsch formatiert";
  public static final String AM_WOCHENENDE = "Die erstellte Klausur liegt am Wochenende";
  public static final String STARTZEIT_VOR_ENDZEIT = "Die Startzeit der Klausur muss vor "
      + "der Endzeit der Klausur liegen";
  public static final String KLAUSUR_IN_ZEITRAUM = "Die beantragte Klausur muss "
      + "im Praktikumszeitraum liegen";
  public static final String UNGUELTIGE_LSFID = "Die angegebene LSF-ID ist ungültig";
  public static final String KLAUSUR_LIEGT_In_DB = "Die Klausur ist schon vorhanden";
  public static final String NEUE_KLAUSUR_SCHNEIDET_ALTE =
      "Die Klausur kann nicht angemeldet werden, weil "
          + "sie sich mit einer bereits angemeldeten Klausur überschneidet";
  public static final String VIELFACHES_VON_15 = "Die Klausurzeit muss ein Vielfaches von 15 sein";
  public static final String NAME_NICHT_LEER = "Der Veranstaltungsname darf nicht leer sein";
  public static final String STORNIERUNG_RECHTZEITIG = "Die Klausur kann nur bis zu "
      + "einem Tag vorher storniert werden";

}
