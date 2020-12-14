package stolexiy.database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Creator {

    private static final String createSeq =
            "CREATE SEQUENCE NAME " +
                    "START WITH START_WITH " +
                    "INCREMENT BY INCREMENT_BY " +
                    "CYCLE " +
                    "CACHE 1;";

    private static final String dropSeq = "DROP SEQUENCE NAME;";

    private static void createTables(Connection connection) throws SQLException {
        ArrayList<String> tableNames = Connector.getTableNames(connection);
        Map<String, ArrayList<String>> columnNames = Connector.getColumnNames(connection, Arrays.stream(Tables.values()).map(Tables::toString).toArray(String[]::new));
        String query;
        Statement st = connection.createStatement();
        for (Tables tableName : Tables.values()) {
            query = "";
            if (tableNames.contains(tableName.toString())) {
                try {
                    st.execute("DROP TABLE " + tableName.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            query = "CREATE TABLE " + tableName.toString() + " (" + String.join(", ", tableName.getFields()) + ");";
            /*else {
                ArrayList<String> columns = columnNames.get(tableName.toString());
                for (String field : tableName.getFields()) {
                    String[] f = field.split(" ");
                    if (!columns.contains(f[0])) {
                        query = "ALTER TABLE " + tableName + " ADD COLUMN " + f[0] + " " + f[1] + ";";
                    }
                }
            }*/
            if (!query.equals("")) System.out.println(query + "\n\t- " + st.executeUpdate(query));
        }
        // Обработка предупреждений от драйвера базы данных
        SQLWarning warn = st.getWarnings();
        while (warn != null) {
            warn.printStackTrace();
            warn = warn.getNextWarning();
        }
        st.close();
    }

    public static void createSequence(Connection connection, String name, int startWith, int increment) {
        try {
            String create = createSeq.replaceFirst("NAME", name);
            create = create.replaceFirst("START_WITH", Integer.toString(startWith));
            create = create.replaceFirst("INCREMENT_BY", Integer.toString(increment));
            try (Statement createSt = connection.createStatement()){
                createSt.executeUpdate(create);
            } catch (SQLException e) {
                Statement dropSt = connection.createStatement();
                String drop = dropSeq.replaceFirst("NAME", name);
                dropSt.executeUpdate(drop);
                dropSt.executeUpdate(create);
                dropSt.close();
            }
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            try (Connection connection = Connector.getConnection()) {
                createTables(connection);
                System.out.println(Connector.getTableNames(connection));
                createSequence(connection, "SEQ_RESULT_ID", 1, 1);
                createSequence(connection, "hibernate_sequence", 1, 1);
            }
        } catch (SQLException e) {
            // Обработка каждого исключения из цепочки
            for (Throwable t : e)
                t.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
