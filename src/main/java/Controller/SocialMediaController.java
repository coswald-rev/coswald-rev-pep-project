package Controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class SocialMediaController {

    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccountService accountService;
    private MessageService messageService;

    /**
     * Default constructor, uses the default AccountService and MessageService constructors.
     */
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * AccountService reciever constructor. 
     * Uses the provided AccountService and the default MessageService constrcutor.
     * 
     * @param accountService
     */
    public SocialMediaController(AccountService accountService) {
        this.accountService = accountService;
        this.messageService = new MessageService();
    }

    /**
     * MessageService reciever constructor. 
     * Uses the provided MessageService and the default AccountService constrcutor.
     * 
     * @param messageService
     */
    public SocialMediaController(MessageService messageService) {
        this.accountService = new AccountService();
        this.messageService = messageService;
    }

    /**
     * AccountService and MessageService reciever constructor.
     * Uses the provided AccountService and MessageService.
     * 
     * @param accountService
     * @param messageService
     */
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // app.get("example-endpoint", this::exampleHandler);

        app.post("register", this::registerHandler);
        app.post("login", this::loginHandler);
        app.post("messages", this::messageCreateHandler);

        app.patch("messages/{message_id}", this::messageUpdateHandler);

        app.get("messages", this::allMessagesHandler);
        app.get("messages/{message_id}", this::messageByIdHandler);

        app.get("accounts/{account_id}/messages", this::messageByAccountIdHandler);

        app.delete("messages/{message_id}", this::messageDeleteByIdHandler);

        return app;
    }

    /**
     * Handler for POST /register
     * 
     * @param context
     * @return the request context
     */
    private Context registerHandler(Context context) {
        String body = context.body();

        try {
            // Unmarshal the body.
            Account account = objectMapper.readValue(body, Account.class);
            
            // Attempt to register.
            Account registeredAccount = accountService.register(account);
            if (registeredAccount == null) {
                return context.status(HttpStatus.BAD_REQUEST);
            }

            // Success, return the new account.
            return context.json(registeredAccount);
        } catch (Exception ex) {
            logger.error("registerHandler threw an exception, body: {}, message: {}", body, ex.getMessage());

            // On error, 400 BAD REQUEST. (Should 500)
            return context.status(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handler for POST /login
     * 
     * @param context
     * @return the request context
     */
    private Context loginHandler(Context context) {
        String body = context.body();

        try {
            // Unmarshal the body.
            Account account = objectMapper.readValue(body, Account.class);

            // Authenticate
            Account authenticatedAccount = accountService.authenticate(account);
            if (authenticatedAccount == null) {
                return context.status(HttpStatus.UNAUTHORIZED);
            }

            // Success, return the authenticated account.
            return context.json(authenticatedAccount);
        } catch (Exception ex) {
            logger.error("loginHandler threw an exception, body: {}, message: {}", body, ex.getMessage());

            // On error, 401 UNAUTHORIZED. (Should 500)
            return context.status(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Handler for POST /messages
     * 
     * @param context
     * @return the request context
     */
    private Context messageCreateHandler(Context context) {
        String body = context.body();
        
        try {
            // Unmarshal the body.
            Message message = objectMapper.readValue(body, Message.class);

            // Create the message.
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage == null) {
                return context.status(HttpStatus.BAD_REQUEST);
            }

            // Success, return the new message.
            return context.json(createdMessage);
        } catch (Exception ex) {
            logger.error("messageCreateHandler threw an exception, body: {}, message: {}", body, ex.getMessage());

            // On error, 400 BAD REQUEST. (Should 500)
            return context.status(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handler for PATCH /messages/{message_id}
     * 
     * @param context
     * @return the request context
     */
    private Context messageUpdateHandler(Context context) {
        String body = context.body();
        String message_id_str = context.pathParam("message_id");

        try {
            // parse the param to int.
            int message_id = Integer.parseInt(message_id_str);

            // Unmarshal the body.
            Message message = objectMapper.readValue(body, Message.class);

            // Update the message.
            Message updatedMessage = messageService.updateMessageById(message.getMessage_text(), message_id);
            if (updatedMessage == null) {
                return context.status(HttpStatus.BAD_REQUEST);
            }

            // Success, return the updated mesage.
            return context.json(updatedMessage);
        } catch (Exception ex) {
            logger.error("messageUpdateHandler threw an exception, message_id_str: {}, body: {}, message: {}", message_id_str, body, ex.getMessage());

            // On error, 400 BAD REQUEST (Should 500).
            return context.status(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handler for GET /messages
     * 
     * @param context
     * @return the request context
     */
    private Context allMessagesHandler(Context context) {
        try {
            // Retrieve all the messages.
            List<Message> messages = messageService.getAllMessages();

            // Success, return the messages.
            return context.json(messages);
        } catch (Exception ex) {
            logger.error("allMessagesHandler threw an exception, message: {}", ex.getMessage());

            // On error, still 200 OK with an empty list.
            return context.json(new ArrayList<Message>());
        }
    }

    /**
     * Handler for GET /messages/{message_id}
     * 
     * @param context
     * @return the request context
     */
    private Context messageByIdHandler(Context context) {
        String message_id_str = context.pathParam("message_id");

        try {
            // parse the param to int.
            int message_id = Integer.parseInt(message_id_str);

            // Get the message.
            Message message = messageService.getMessageById(message_id);
            if (message == null) {
                // On null, still 200 OK.
                return context.status(HttpStatus.OK);
            }

            // Success, return the message.
            return context.json(message);
        } catch (Exception ex) {
            logger.error("messageByIdHandler threw an exception, message_id_str: {}, message: {}", message_id_str, ex.getMessage());

            // On error, still 200 OK.
            return context.status(HttpStatus.OK);
        }
    }

    /**
     * Handler for GET /accounts/{account_id}/messages
     * @param context
     * @return
     */
    private Context messageByAccountIdHandler(Context context) {
        String account_id_str = context.pathParam("account_id");

        try {
            // parse the param to int.
            int account_id = Integer.parseInt(account_id_str);

            // Retrieve all the messages by account_id.
            List<Message> messages = messageService.getAllMessagesByAccountId(account_id);

            // Success, return the messages.
            return context.json(messages);
        } catch (Exception ex) {
            logger.error("messageByAccountIdHandler threw an exception, account_id_str: {}, message: {}", account_id_str, ex.getMessage());

            // On error, still 200 OK with an empty list.
            return context.json(new ArrayList<Message>());
        }
    }

    /**
     * Handler for DELETE /messages/{message_id}
     * 
     * @param context
     * @return the request context
     */
    private Context messageDeleteByIdHandler(Context context) {
        String message_id_str = context.pathParam("message_id");

        try {
            // parse the param to int.
            int message_id = Integer.parseInt(message_id_str);

            // Delete the message.
            Message deletedMessage = messageService.deleteMessageById(message_id);
            if (deletedMessage == null) {
                // On null, still 200 OK.
                return context.status(HttpStatus.OK);
            }

            // Success, return the deleted message.
            return context.json(deletedMessage);
        } catch (Exception ex) {
            logger.error("messageDeleteByIdHandler threw an exception, message_id_str: {}, message: {}", message_id_str, ex.getMessage());

            // On error, still 200 OK.
            return context.status(HttpStatus.OK);
        }
    }
}