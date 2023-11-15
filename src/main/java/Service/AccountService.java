package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    
    private AccountDAO accountDAO;

    /**
     * Default constructor when no AccountDAO is provided.
     */
    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    /**
     * Constructor when an AccountDAO is provided.
     * 
     * @param accountDAO
     */
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Attempt to register a new account.
     * Requirements:
     *  username is not blank
     *  password is at least 4 characters
     *  account with username does not already exist
     * 
     * @param account the account to register.
     * @return the newly registered account, null if the registration failed.
     */
    public Account register(Account account) {
        // Ensure username is not blank.
        if (account.getUsername().isBlank()) {
            return null;
        }

        // Ensure password is at least 4 characters.
        if (account.getPassword().length() < 4) {
            return null;
        }

        // Ensure account does not already exist.
        if (accountDAO.getAccountByUserame(account.getUsername()) != null) {
            return null;
        }

        return accountDAO.insertAccount(account);
    }

    /**
     * Attempt to authenticate against an existing account.
     * Requirements:
     *  account exists
     *  passwords are the same
     * 
     * @param account the account containing the username and password to authentcate against.
     * @return the account containing the account_id if authentication was successful, null if: the account doesn't exist, the passwords didn't match
     */
    public Account authenticate(Account account) {
        // Ensure account exists.
        Account existingAccount = accountDAO.getAccountByUserame(account.getUsername());
        if (existingAccount == null) {
            return null;
        }

        // Ensure password matches.
        if (!existingAccount.getPassword().equals(account.getPassword())) {
            return null;
        }

        return existingAccount;
    }
}
