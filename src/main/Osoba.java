package main;

import java.io.Serializable;

class Osoba implements Serializable {
    
    String jmeno;
    RodnéČíslo rc;

    public Osoba(String jmeno, RodnéČíslo rc) {
        this.jmeno = jmeno;
        this.rc = rc;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getRC() {
        return rc.toString();
    }

    public void setRC(String rc) {
        this.rc.setRC(rc);
    }
    
}
