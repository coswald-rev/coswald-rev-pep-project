package Service;

import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    /**
     * Default constructor when no MessageDAO is provided.
     */
    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    /**
     * Constructor when an MessageDAO is provided.
     * 
     * @param messageDAO
     */
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = new AccountDAO();
    }

    /**
     * Constructor when an AccountDAO is provided.
     * 
     * @param accountDAO
     */
    public MessageService(AccountDAO accountDAO) {
        this.messageDAO = new MessageDAO();
        this.accountDAO = accountDAO;
    }

    /**
     * Constructor when a MessageDAO and AccountDAO are provided.
     * 
     * @param messageDAO
     * @param accountDAO
     */
    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    /**
     * Get all the messages.
     * 
     * @return a list containing all the messages, the list is empty if there are no messages.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }
    
    /**
     * Get all the messages created by a given account.
     * 
     * @param account_id the account id to filter for.
     * @return a list containing all the messages, the list is empty if there are no messages.
     */
    public List<Message> getAllMessagesByAccountId(int account_id) {
        return messageDAO.getAllMessagesByAccountId(account_id);
    }

    /**
     * Get a message by message_id.
     * 
     * @param message_id the id of the message.
     * @return the message if one exists, null if none exists.
     */
    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    /**
     * Create a new message.
     * Requirements:
     *  message_text is not blank
     *  0 < message_text.length < 255
     *  posted_by relates to an existing user
     * 
     * @param message the message to create.
     * @return the new message or null if: a requirement was unmet, an error occurred.
     */
    public Message createMessage(Message message) {
        // Ensure message_text is valid.
        if (!messageTextIsValid(message.getMessage_text())) {
            return null;
        }

        // Ensure posted_by has an existing user.
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            return null;
        }
        
        return messageDAO.insertMessage(message);
    }

    /**
     * Update a message by message id.
     * Requirements:
     *  message_text is not blank
     *  0 < message_text.length < 255
     * 
     * @param message_text the new message text.
     * @param message_id the id of the message.
     * @return the updated message, null if: the message doesn't exist, message_text is invalid, an error occurred.
     */
    public Message updateMessageById(String message_text, int message_id) {
        // Ensure the message exists.
        Message existingMessage = messageDAO.getMessageById(message_id);
        if (existingMessage == null) {
            return null;
        }

        // Ensure message text is valid.
        if (!messageTextIsValid(message_text)) {
            return null;
        }

        // Attempt the update.
        if (!messageDAO.updateMessageById(message_text, message_id)) {
            return null;
        }

        // Return the updated message.
        return getMessageById(message_id);
    }

    /**
     * Delete a message by message_id.
     * Requirements:
     *  the message must exist.
     * 
     * @param message_id the id of the message.
     * @return the message that was deleted, null if: the message doesn't exist, there was an error deleting the message.
     */
    public Message deleteMessageById(int message_id) {
        // Ensure the message exists.
        Message existingMessage = messageDAO.getMessageById(message_id);
        if (existingMessage == null) {
            return null;
        }

        // Ensure we successfully deleted the message.
        if (!messageDAO.deleteMessageById(message_id)) {
            return null;
        }

        return existingMessage;
    }

    /**
     * Helper method to ensure Message.message_text is valid.
     * Requirements:
     *  message_text is not blank
     *  message_text is <= 255 characters.
     *  e.g. 0 < message_text.length <= 255
     * 
     * @param message_text the text to validate
     * @return true if valid, false if invalid.
     */
    private boolean messageTextIsValid(String message_text) {
        return !message_text.isBlank() && message_text.length() < 255;
    }
}
