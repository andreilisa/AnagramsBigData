import java.io.*;
import java.sql.*;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost:5432/anagramsDB";
        String username = "postgres";
        String password = "free";
        String strCurrentLine;
        TreeSet<String> treeSet = new TreeSet<>();

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out))) {

            BufferedReader objReader = new BufferedReader(new FileReader("C:\\Users\\andrei.lisa\\IdeaProjects\\qwe\\folder\\file1.txt"));
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.setFetchSize(100000);
            while ((strCurrentLine = objReader.readLine()) != null) {

                ResultSet resultSet = statement.executeQuery(String.format("with context as (\n" +
                        "    select id, word, array(select distinct unnest(regexp_split_to_array(word, '')) x order by x) as charset from dict\n" +
                        ")\n" +
                        "select id, word from context where charset=(select charset from context where word='%s' limit 1)", strCurrentLine));

                while (resultSet.next()) {
                    treeSet.add(resultSet.getString(2));
                    Statement statement1 = connection.createStatement();
                    statement1.executeUpdate(
                            String.format("delete from dict where id =%s", resultSet.getString(1))
                    );
                }
                if (treeSet.size() > 1)
                    System.out.println(treeSet);
                treeSet.clear();

                resultSet.close();
            }


        } catch (SQLException | IOException throwable) {
            throwable.printStackTrace();
        }

    }
}

