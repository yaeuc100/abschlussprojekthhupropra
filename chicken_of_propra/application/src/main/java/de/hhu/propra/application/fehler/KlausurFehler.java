package de.hhu.propra.application.fehler;

public class KlausurFehler {
    public static final String STARTZEIT_VOR_ENDZEIT = "Die Startzeit der Klausur muss vor der Endzeit des Urlaubs liegen";
    public static final String KLAUSUR_IN_ZEITRAUM = "Der beantragte Klausur muss in Praktikumszeitraum liegen";
    public static final String UNGUELTIGE_LSFID = "Die angegebene LSF-iD ist ungültig";
    public static final String KLAUSUR_LIEGT_In_DB = "Die Klausur ist schon vorhanden";
    public static final String NEUE_KLAUSUR_SCHNEIDET_ALTE = "Die Klausur kann nicht angemeldet werden, weil " +
            "sie sich mit einer andern bereits angemedelten überschneidet";
}
