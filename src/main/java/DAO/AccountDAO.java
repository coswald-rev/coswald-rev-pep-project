package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    /**
     * Retrieves an Account by the provided username.
     * 
     * @param username the name of the account.
     * @return the account if one exists, null if none exists.
     */
    public Account getAccountByUserame(String username) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "SELECT * FROM account WHERE username = ?";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account(
                        rs.getInt("account_id"), 
                        rs.getString("username"), 
                        rs.getString("password")
                    );
                return account;
            }
        } catch (SQLException ex) {
            logger.error("getAccountByUsername threw an exception, username: {}, message: {}", username, ex.getMessage());
        }

        return null;
    }

    /**
     * Attempts to insert the provided Account.
     * 
     * @param account the account to insert.
     * @return the account with the account_id field set to the inserted id, null if something went wrong.
     */
    public Account insertAccount(Account account) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "INSERT INTO account (username, password) VALUES (?, ?)";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                int inserted_id = rs.getInt("account_id");
                account.setAccount_id(inserted_id);

                return account;
            }
        } catch (SQLException ex) {
            logger.error("insertAccount threw an exception, account: {}, message: {}", account, ex.getMessage());
        }

        return null;
    }

}
