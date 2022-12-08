package org.growhack.bank.portal.dao;

import org.growhack.bank.portal.entity.TransactitonBankEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    private static String DB_URL = "jdbc:mysql://localhost:3306/ffm_db"
            + "databaseName=BankingSystem;"
            + "integratedSecurity=true";
    private static String USER_NAME = "root";
    private static String PASSWORD = "Haideptraiok";

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            System.out.println("connect successfully");
            return conn;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("connect failed!!!");
        return conn;
    }

    public boolean updateAccount(TransactitonBankEntity transactitonBankEntity) {
        try (Connection connection = DBConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO transaction_bank(sender_id, send_account, bank_of_sender, amount, messages, received_id, received_account, bank_of_receiver, money_balance, transaction_id, created_time, ) VALUES(?,?,?,?,?,?,?,?,?,?,?);");

            statement.setString(1, transactitonBankEntity.getSenderId());
            statement.setString(2, transactitonBankEntity.getSendAccount());
            statement.setString(3, transactitonBankEntity.getBankOfSender());
            statement.setLong(4, transactitonBankEntity.getAmount());
            statement.setString(5, transactitonBankEntity.getMessages());
            statement.setString(6, transactitonBankEntity.getReceivedId());
            statement.setString(7, transactitonBankEntity.getReceivedAccount());
            statement.setString(8, transactitonBankEntity.getBankOfReceiver());
            statement.setLong(9, transactitonBankEntity.getMoneyBalance());
            statement.setString(10, transactitonBankEntity.getTransactionId());
            statement.setDate(11, transactitonBankEntity.getCreatedTime());

            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
