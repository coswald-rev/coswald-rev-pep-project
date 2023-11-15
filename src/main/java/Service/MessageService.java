package Service;

import DAO.MessageDAO;

public class MessageService {
    
    private MessageDAO messageDAO;

    /**
     * Default constructor when no MessageDAO is provided.
     */
    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    /**
     * Constructor when an messageDAO is provided.
     * 
     * @param messageDAO
     */
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    // #3 in readme.
}
