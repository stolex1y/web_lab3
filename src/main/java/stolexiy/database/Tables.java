package stolexiy.database;

import java.util.ArrayList;
import java.util.Arrays;

public enum Tables {
    USERS_WEB(new String[]{
            "LOGIN VARCHAR(20) PRIMARY KEY",
            "PASS VARCHAR(128) NOT NULL",
            "SALT VARCHAR(30) NOT NULL"}),

    RESULTS(new String[] {
            "ID INTEGER PRIMARY KEY",
            "LOGIN VARCHAR(50) NOT NULL",
            "DATE TIMESTAMP NOT NULL",
            "LEAD_TIME INTEGER NOT NULL",
            "X REAL NOT NULL",
            "Y REAL NOT NULL",
            "R REAL NOT NULL",
            "HIT BOOLEAN NOT NULL"});





    Tables(String[] fields) {
        this.fields = new ArrayList<>(Arrays.asList(fields));
    }

    private final ArrayList<String> fields;

    public ArrayList<String> getFields() {
        return fields;
    }
}
