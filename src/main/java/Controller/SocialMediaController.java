package Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
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

        return app;
    }

    /**
     * Handler for POST /register
     * 
     * @param context
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

            return context.status(HttpStatus.BAD_REQUEST);
        }
    }

    private void loginHandler(Context context) {}
}