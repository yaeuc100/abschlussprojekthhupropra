package de.hhu.propra.application.fehler;

import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.utils.UrlaubValidierung;

import java.util.HashMap;

public class UrlaubFehler {

    public static final String VIELFACHES_VON_15 = "Genommener Urlaub muss ein Vielfaches von 15 sein";
    public static final String DAUER_IST_VALIDE = "Die Dauer kan nur 240 min betragen oder weniger als 150 min ";
    public static final String ANTRAG_RECHTZEITIG = "Urlaub kann nur bis zu einem Tag vorher beantragt werden";
    public static final String STARTZEIT_VOR_ENDZEIT = "Die Startzeit des Urlaubs muss vor der Endzeit des Urlaubs liegen";
    public static final String URLAUB_IN_ZEITRAUM = "Der beantragte Urlaub muss in Praktikumszeitraum liegen";
    public static final String ZWEI_URLAUB_AN_TAG = "Zwei genommene Urlaube an einem Tag können nur " +
            "am Anfang und am Ende des Tages liegen" +
            "+ es müssen 90 Minuten dazwischen liegen";
    public static final String NICHT_GENUG_URLAUB_VORHANDEN = "Der Resturlaub reicht nicht für diesen Urlaubsantrag";
    public static final String MAX_ZWEI_URLAUBE = "Es darf nur max zwei Urlaube an einem Tag geben";
    /*public static final String
    public static final String*/


}
