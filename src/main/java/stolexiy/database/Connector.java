package stolexiy.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Connector {
    // Файл, определяющий пользователя и базу данных для подключения
    private static final String fileProps = "/database.properties";
    private static SessionFactory factory = null;

    // Перец, добавляется к паролю перед хешированием
    private static final String pepper = "xqVa0vE+#S.1jx(\\";

    public static ArrayList<String> getTableNames(Connection connection) throws SQLException {
        ArrayList<String> tableNames = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet mrs = meta.getTables
                (null, null, null, new String[] { "TABLE" });
        while (mrs.next()) {
            tableNames.add(mrs.getString(3).toUpperCase());
        }
        mrs.close();
        return tableNames;
    }

    public static Map<String, ArrayList<String>> getColumnNames(Connection connection, String... tableNames) throws SQLException {
        Map<String, ArrayList<String>> columnNames = new HashMap<>();
        Statement st = connection.createStatement();
        ResultSetMetaData meta;
        ResultSet resultSet = null;
        for (String tableName : tableNames) {
            ArrayList<String> namesList = new ArrayList<>();
            try {
                String query = "SELECT * FROM " + tableName + " LIMIT 1";
                resultSet = st.executeQuery(query);
                meta = resultSet.getMetaData();
            } catch (SQLException e) {
                continue;
            }
            int columnCount = meta.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                namesList.add(tableName + "." + meta.getColumnName(i).toUpperCase());
            }
            columnNames.put(tableName, namesList);
        }
        if (resultSet != null) resultSet.close();
        return columnNames;
    }

    // Данный метод получает соединение с базой данных из свойств,
    // определенных в файле с названием, лежащим в строке fileProps
    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream in = Connector.class.getResourceAsStream(fileProps)) {
            props.load(in);
        }
        DriverManager.setLoginTimeout(30);
        // Регистрирует драйвер, если он указан в файле
        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) System.setProperty("jdbc.drivers", drivers);
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url, username, password);
    }

    public static SessionFactory getSessionFactory() {
        if (factory == null)
            factory = new Configuration().configure().addAnnotatedClass(stolexiy.Point.class).buildSessionFactory();
        return factory;
    }
}
