package com.monkey.acromvote;

import java.sql.*;

public class Database {
    private static Connection connection;
    private static AcromVote plugin;
    static String green = "\u001B[38;2;15;252;3m";
    static String yellow = "\u001B[38;2;255;255;0m";
    static String orange = "\u001B[38;2;255;165;0m";

    static String table = "vote_party_stats";

    public Database(AcromVote plugin) {
        this.plugin = plugin;
    }

    public static Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }

        String databaseSelect = "Plugin";
        String host = "172.18.0.1:25567";

        String url = "jdbc:mysql://" + host + "/" + databaseSelect;
        String user = "TopolinoMangiaPisello";
        String password = "Pisellone12Macumba";

        connection = DriverManager.getConnection(url, user, password);

        System.out.println(green + "Connesso al database MySQL: " + orange + databaseSelect);

        return connection;
    }

    public static boolean checkAndReconnect() {

        String databaseSelect = "Plugin";
        String host = "172.18.0.1:25567";

        String url = "jdbc:mysql://" + host + "/" + databaseSelect;
        String user = "TopolinoMangiaPisello";
        String password = "Pisellone12Macumba";

        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }


            connection = DriverManager.getConnection(url, user, password);
            System.out.println(green + "Riconnesso al database MySQL: " + orange + databaseSelect);
            return true;
        } catch (SQLException e) {
            System.out.println("Errore durante il tentativo di riconnessione al database:");
            e.printStackTrace();
            return false;
        }
    }

    public static void initializeDatabase() throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableResultSet = metaData.getTables(null, null, "vote_party_stats", null);

        if (!tableResultSet.next()) {
            String sql = "CREATE TABLE " + table +  " (Vote int, Donation int, HypeParty boolean, HypePartyActiveByDatabase boolean, HypePartDiscordMessage boolean, HypePartTimer boolean, MaxVote int, MaxDonation int, HypePartyCount int)";
            statement.execute(sql);


            String insertSql = "INSERT INTO " + table +  " (Vote, Donation, HypeParty, HypePartyActiveByDatabase, HypePartDiscordMessage, HypePartTimer, MaxVote, MaxDonation, HypePartyCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                insertStatement.setInt(1, 0);
                insertStatement.setInt(2, 0);
                insertStatement.setBoolean(3, false);
                insertStatement.setBoolean(4, false);
                insertStatement.setBoolean(5, false);
                insertStatement.setBoolean(6, false);
                insertStatement.setInt(7, 0);
                insertStatement.setInt(8, 0);
                insertStatement.setInt(9, 0);
                insertStatement.executeUpdate();
            }

            System.out.println(green + "Tabella " + orange + table + green + " creata e inizializzata!");
        } else {
            System.out.println(yellow + "La tabella " + orange + table + yellow + " esiste già nel database.");
        }

        tableResultSet.close();
        statement.close();
    }

    public static void setMaxVote(int maxVote) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET MaxVote = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, maxVote);
            preparedStatement.executeUpdate();

            System.out.println("Valori di MaxDonation impostati con successo nel database");
        } catch (SQLException e) {
            System.out.println("Errore nell'impostare i valori di MaxDonation nel database");
            e.printStackTrace();
        }
    }


    public static void setMaxDonation(int maxDonation) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET MaxDonation = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, maxDonation);
            preparedStatement.executeUpdate();

            System.out.println("Valori di MaxDonation impostati con successo nel database");
        } catch (SQLException e) {
            System.out.println("Errore nell'impostare i valori di MaxDonation nel database");
            e.printStackTrace();
        }
    }

    public static int getHypePartyCountFromDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT HypePartyCount FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("HypePartyCount");
            } else {
                return 0;
            }
        }
    }

    public static void updateHypePartyCountInDatabase(int newHypePartyCount) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET HypePartyCount = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, newHypePartyCount);
            preparedStatement.executeUpdate();

            System.out.println(green + "Conteggo dell'hype party nel database aggiornato a: " + orange + newHypePartyCount);
        }
    }

    public static void resetHypePartyCountInDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET HypePartyCount = 0";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();

            System.out.println(green + "Il conteggio degli hype party nel database è stato azzerato.");
        }
    }

    public static int getVoteCountFromDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT Vote FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("Vote");
            } else {
                return 0;
            }
        }
    }

    public static void updateVoteCountInDatabase(int newVoteCount) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET Vote = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, newVoteCount);
            preparedStatement.executeUpdate();

            System.out.println(green + "Voto nel database aggiornato a: " + orange + newVoteCount);
        }
    }

    public static void resetVoteCountInDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET Vote = 0";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();

            System.out.println(green + "Il conteggio dei voti nel database è stato azzerato.");
        }
    }


    public static int getDonationCountFromDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT Donation FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("Donation");
            } else {
                return 0;
            }
        }
    }

    public static void updateDonationCountInDatabase(int newDonationCount) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET Donation = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, newDonationCount);
            preparedStatement.executeUpdate();

            System.out.println(green + "Donazione nel database aggiornato a: " + orange + newDonationCount);
        }
    }

    public static void resetDonationCountInDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table +  " SET Donation = 0";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();

            System.out.println(green + "Il conteggio delle donazioni nel database è stato azzerato.");
        }
    }

    public static boolean isHypePartyActive() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT HypeParty FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBoolean("HypeParty");
            } else {
                return false;
            }
        }
    }

    public static void setHypePartyActive(boolean isActive) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET HypeParty = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isActive);
            preparedStatement.executeUpdate();

            System.out.println(green + "Stato HypeParty nel database impostato a: " + orange + isActive);
        }
    }

    public static boolean isHypePartyActiveByDatabase() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT HypePartyActiveByDatabase FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBoolean("HypePartyActiveByDatabase");
            } else {
                return false;
            }
        }
    }

    public static void setHypePartyActiveByDatabase(boolean isActiveByDatabase) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET HypePartyActiveByDatabase = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isActiveByDatabase);
            preparedStatement.executeUpdate();

            System.out.println(green + "Stato HypePartyActiveByDatabase nel database impostato a: " + orange + isActiveByDatabase);
        }
    }

    public static boolean isHypePartyDiscordMessageSent() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT HypePartDiscordMessage FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBoolean("HypePartDiscordMessage");
            } else {
                return false;
            }
        }
    }

    public static void setHypePartyDiscordMessageSent(boolean isSentDiscordMessage) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET HypePartDiscordMessage = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isSentDiscordMessage);
            preparedStatement.executeUpdate();

            System.out.println(green + "Stato HypePartDiscordMessage nel database impostato a: " + orange + isSentDiscordMessage);
        }
    }

    public static boolean isHypePartyTimerActive() throws SQLException {
        Connection connection = getConnection();
        String sql = "SELECT HypePartTimer FROM " + table;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBoolean("HypePartTimer");
            } else {
                return false;
            }
        }
    }

    public static void setHypePartyTimerActive(boolean isTimerActive) throws SQLException {
        Connection connection = getConnection();
        String sql = "UPDATE " + table + " SET HypePartTimer = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isTimerActive);
            preparedStatement.executeUpdate();

            System.out.println(green + "Stato HypePartTimer nel database impostato a: " + orange + isTimerActive);
        }
    }
}
