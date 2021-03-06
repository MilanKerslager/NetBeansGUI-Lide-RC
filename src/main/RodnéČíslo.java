package main;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RodnéČíslo implements Serializable {

    String rc;

    public RodnéČíslo(String rc) {
        this.rc = rc;
    }

    // aby šlo rodné číslo vypsat (předefinování implicitní metody toString())
    @Override
    public String toString() {
        return rc;
    }

    // změna rodného čísla
    public void setRC(String rc) {
        this.rc = rc;
    }

    public boolean isMuž() {
        if (Integer.valueOf(rc.substring(2, 4)) <= 12) {
            return true;
        } else {
            return false;
        }
    }

    public String getDatumNarozeni() {
        return rc.substring(4, 6) + "."
                + rc.substring(2, 4) + "."
                + rc.substring(0, 2);
    }

    public static boolean isOk(String rcislo) {
        // rozebereme RC na jednotlivé části
        // TODO: ověřit kontrolu RČ se třemi ciframi za lomítkem
        String regex = "^\\s*(\\d\\d)(\\d\\d)(\\d\\d)[ /]*(\\d\\d\\d)(\\d?)\\s*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(rcislo);
        String rok, mesic, den, ext, kontrolni = null;
        if (m.find()) {
            rok = m.group(1);
            mesic = m.group(2);
            den = m.group(3);
            ext = m.group(4);
            kontrolni = m.group(5);
        } else {
            // nejde-li rozparsovat podle regulárního výrazu, je to chyba
            return false;
        }

        // kontrola dělitelnosti RČ
        int suma = 0;
        // FIXME: vyházet znaky podle regulárního výrazu
        String rodne = rcislo.replaceAll("/", "");
        for (int i = 0; i <= rodne.length() - 2; i++) {
            // vynecháme lomítka
            if (Character.isDigit(rodne.charAt(i)) == true) {
                // liché pozice přičítáme, sudé odečítáme
                if (i % 2 == 0) {
                    suma += Character.getNumericValue(rodne.charAt(i));
                } else {
                    suma -= Character.getNumericValue(rodne.charAt(i));
                }
            }
        }
        // zbytek po dělení 11 z výše vypočítaného součtu
        int zbytek = suma % 11;
        // pokud je zbytek 10, bude kontrolní (poslední) číslice nula
        if (zbytek == 10) {
            zbytek = 0;
        }

        // doplnění století v roku (rok na 4 čísla)
        int celyrok = Integer.parseInt(rok);
        if (kontrolni.isEmpty()) {
            // do roku 1953 byla RČ krátká a narození muselo být 19??
            celyrok += 1900;
        } else {
            // od roku 1954 byla RČ dlouhá a narození může být po r. 2000
            // tj. od roku 2054 nejsou RČ definovaná
            celyrok += (celyrok < 54) ? 2000 : 1900;
        }

        // korekce měsíce (žena a další speciality zavedené po roce 2003)
        int mes = Integer.parseInt(mesic);
        if (mes > 70 && celyrok > 2003) {
            mes -= 70;
        } else if (mes > 50) {
            mes -= 50;
        } else if (mes > 20 && celyrok > 2003) {
            mes -= 20;
        }

        // do roku 1953 není kontrolní číslice
        if (celyrok < 1954 && ! kontrolni.isEmpty()) {
            return false;
        }

        // od roku 1954 musí být kontrolní číslice
        if (celyrok >= 1954 && kontrolni.isEmpty()) {
            return false;
        }

        // když máme kontrolní číslici, tak by zbytek po dělení
        // měl být roven kontrolní číslici
        if (! kontrolni.isEmpty() && zbytek != Integer.parseInt(kontrolni)) {
            // když není roven, je to chyba
            return false;
        }

        // korigovaný měsíc musíme mít ve dvou cifrách (01, ... 12)
        mesic = "0" + mes;
        mesic = mesic.substring(mesic.length() - 2);
        // kontrola reálnosti datumu
        // FIXME: 150229 kontrolou projde!!!
        DateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        try {
            fmt.parse("" + celyrok + mesic + den);
        } catch (ParseException ex) {
            // datum není reálný
            return false;
        }

        // TODO: RČ nesmí být v budoucnosti
        // došli jsme až sem, takže žádná chyba nebyla v RČ nalezena
        return true;
    }
}
