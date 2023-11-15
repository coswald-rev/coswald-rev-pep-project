package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {

    private static final Logger logger = LoggerFactory.getLogger(MessageDAO.class);

    /**
     * Retrieves all the Messages.
     * 
     * @return a list containing all the messages.
     */
    public List<Message> getAllMessages() {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String query = "SELECT * FROM message";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                messages.add(getMessageFromResultSet(rs));
            }
        } catch (SQLException ex) {
            logger.error("getAllMessages threw an exception, message: {}", ex.getMessage());
        }

        return messages;
    }

    /**
     * Retrieves all messages by account id.
     * 
     * @param account_id the account_id of the posts
     * @return a list containing all the messages
     */
    public List<Message> getAllMessagesByAccountId(int account_id) {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String query = "SELECT * FROM message WHERE posted_by = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, account_id);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                messages.add(getMessageFromResultSet(rs));
            }
        } catch (SQLException ex) {
            logger.error("getAllMessagesByAccountId threw an exception, account_id: {}, message: {}", account_id, ex.getMessage());
        }

        return messages;
    }

    /**
     * Retrieve a message by message_id.
     * 
     * @param message_id the id of the message.
     * @return the message if one exists, null if none exists.
     */
    public Message getMessageById(int message_id) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "SELECT * FROM message WHERE message_id = ?";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, message_id);

            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return getMessageFromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.error("getMessageById threw an exception, message_id: {}, messge: {}", message_id, ex.getMessage());
        }

        return null;
    }

    /**
     * Attempts to insert the provided Message.
     * 
     * @param message the message to insert.
     * @return the message with the message_id set to the inserted id, null if something went wrong.
     */
    public Message insertMessage(Message message) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int inserted_id = rs.getInt("message_id");
                message.setMessage_id(inserted_id);

                return message;
            }
        } catch (SQLException ex) {
            logger.error("insertMessage threw an excetpion, message_obj: {}, message: {}", message, ex.getMessage());
        }

        return null;
    }

    /**
     * Attempt to update a Message text by the given message_id.
     * 
     * @param message_text the new text of the message.
     * @param message_id the id of the message to update.
     * @return the updated message if the update was successful, null if: something went wrong, message didn't exist
     */
    public Message updateMessageById(String message_text, int message_id) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "UPDATE message SET message_text = ? WHERE message_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, message_text);
            ps.setInt(2, message_id);

            ps.executeUpdate();

            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                return getMessageFromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.error("updateMessageById threw an exception, message_text: {}, message_id: {}, message: {}", message_text, message_id, ex.getMessage());
        }

        return null;
    }

    /**
     * Attempt to delete a Message by the given message_id.
     * @param message_id the id of the message.
     * @return true if the record was deleted, false if: something went wrong, the message didn't exist, or no rows were affected.
     */
    public boolean deleteMessageById(int message_id) {
        Connection conn = ConnectionUtil.getConnection();

        try {
            String query = "DELETE FROM message WHERE message_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, message_id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.error("deleteMessageById threw an exception, message_id: {}, message: {}", message_id, ex.getMessage());
        }

        return false;
    }

    /**
     * Helper method to extract the Message from the supplied ResultSet.
     * 
     * @param rs the ResultSet containing the message.
     * @return the Message created from the supplied ResultSet.
     * @throws SQLException
     */
    private Message getMessageFromResultSet(ResultSet rs) throws SQLException {
        return new Message(
            rs.getInt("message_id"), 
            rs.getInt("posted_by"), 
            rs.getString("message_text"), 
            rs.getLong("time_posted_epoch")
        );
    }
}
