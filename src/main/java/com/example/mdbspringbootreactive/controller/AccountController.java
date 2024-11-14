package com.example.mdbspringbootreactive.controller;


import com.example.mdbspringbootreactive.entity.TransferRequest;

import com.example.mdbspringbootreactive.exception.AccountNotFoundException;
import com.example.mdbspringbootreactive.model.Account;
import com.example.mdbspringbootreactive.model.Txn;
import com.example.mdbspringbootreactive.model.TxnEntry;
import com.example.mdbspringbootreactive.repository.AccountRepository;
import com.example.mdbspringbootreactive.service.TxnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST controller for managing account-related operations, including account creation,
 * retrieval, debit and credit transactions, and fund transfers.
 */
@RestController
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository accountRepository;
    private final TxnService txnService;

    public AccountController(AccountRepository accountRepository, TxnService txnService) {
        this.accountRepository = accountRepository;
        this.txnService = txnService;
    }

    /**
     * Endpoint for creating a new account.
     * @param account The account object from the request body.
     * @return The saved account.
     */
    @PostMapping("/account")
    public Mono<Account> createAccount(@RequestBody Account account) {
        LOGGER.info("Creating a new account");
        return accountRepository.save(account);
    }

    /**
     * Retrieves an account based on the account number.
     * @param accountNum The account number.
     * @return The account details or an error if not found.
     */
    @GetMapping("/account/{accountNum}")
    public Mono<Account> getAccount(@PathVariable String accountNum) {
        LOGGER.info("Fetching account with number: {}", accountNum);
        return accountRepository.findByAccountNum(accountNum)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account not found")));
    }

    /**
     * Debits an account with the specified amount.
     * @param accountNum The account number.
     * @param requestBody A map containing the amount to debit.
     * @return The resulting transaction.
     */
    @PostMapping("/account/{accountNum}/debit")
    public Mono<Txn> debitAccount(@PathVariable String accountNum, @RequestBody Map<String, Object> requestBody) {
        LOGGER.info("Debiting account number: {}", accountNum);
        double amount = ((Number) requestBody.getOrDefault("amount", 0)).doubleValue();
        Txn txn = new Txn();
        txn.addEntry(new TxnEntry(accountNum, amount));
        return txnService.saveTransaction(txn).flatMap(txnService::executeTxn);
    }

    /**
     * Credits an account with the specified amount.
     * @param accountNum The account number.
     * @param requestBody A map containing the amount to credit.
     * @return The resulting transaction.
     */
    @PostMapping("/account/{accountNum}/credit")
    public Mono<Txn> creditAccount(@PathVariable String accountNum, @RequestBody Map<String, Object> requestBody) {
        LOGGER.info("Crediting account number: {}", accountNum);
        double amount = ((Number) requestBody.getOrDefault("amount", 0)).doubleValue();
        Txn txn = new Txn();
        txn.addEntry(new TxnEntry(accountNum, -amount));
        return txnService.saveTransaction(txn).flatMap(txnService::executeTxn);
    }

    /**
     * Transfers funds from one account to another.
     * @param from The source account number.
     * @param transferRequest The transfer details, including the target account and amount.
     * @return The resulting transaction.
     */
    @PostMapping("/account/{from}/transfer")
    public Mono<Txn> transfer(@PathVariable String from, @RequestBody TransferRequest transferRequest) {
        LOGGER.info("Transferring funds from account: {} to account: {}", from, transferRequest.getTo());
        Txn txn = new Txn();
        txn.addEntry(new TxnEntry(from, -transferRequest.getAmount()));
        txn.addEntry(new TxnEntry(transferRequest.getTo(), transferRequest.getAmount()));
        return txnService.saveTransaction(txn).flatMap(txnService::executeTxn);
    }


}
