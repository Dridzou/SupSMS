package com.example.hugo.supsms;

/**
 * Created by Hugo on 04/02/2015.
 */
public class Contact {

    public Integer _ID;
    public String DNAME;
    public String EMAIL;
    public String PNUMBER;

    public Contact(Integer _ID, String DNAME, String PNUMBER, String EMAIL) {
        this._ID = _ID;
        this.DNAME = DNAME;
        this.PNUMBER = PNUMBER;
        this.EMAIL = EMAIL;
    }

    public String getDNAME() {
        return DNAME;
    }

    public void setDNAME(String DNAME) {
        this.DNAME = DNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPNUMBER() {
        return PNUMBER;
    }

    public void setPNUMBER(String PNUMBER) {
        this.PNUMBER = PNUMBER;
    }
}
