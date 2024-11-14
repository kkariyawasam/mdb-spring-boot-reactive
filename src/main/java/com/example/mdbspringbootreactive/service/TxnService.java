package com.example.mdbspringbootreactive.service;

import com.example.mdbspringbootreactive.enumeration.ErrorReason;
import com.example.mdbspringbootreactive.enumeration.TxnStatus;
import com.example.mdbspringbootreactive.exception.AccountNotFoundException;
import com.example.mdbspringbootreactive.exception.TransactionException;
import com.example.mdbspringbootreactive.model.Txn;
import com.example.mdbspringbootreactive.repository.AccountRepository;
import com.example.mdbspringbootreactive.template.TxnTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Service to manage transactions and account balance updates.
 * Utilizes reactive programming patterns and transaction management with TransactionalOperator.
 */
@Service
public class TxnService {
	
    private final TxnTemplate txnTemplate;
    private final AccountRepository accountRepository;
    private final TransactionalOperator transactionalOperator;

    /**
     * Constructs a TxnService with required dependencies.
     *
     * @param txnTemplate          the template for transaction persistence
     * @param accountRepository    repository for account data
     * @param transactionalOperator operator to manage transactions reactively
     */
    public TxnService(TxnTemplate txnTemplate, AccountRepository accountRepository,
                      TransactionalOperator transactionalOperator) {
        this.txnTemplate = txnTemplate;
        this.accountRepository = accountRepository;
        this.transactionalOperator = transactionalOperator;
    }

    /**
     * Saves a new transaction to the database.
     *
     * @param txn the transaction to be saved
     * @return Mono emitting the saved transaction
     */
    public Mono<Txn> saveTransaction(Txn txn) {
        return txnTemplate.save(txn);
    }

    /**
     * Executes a transaction by updating account balances and setting transaction status.
     * Errors related to insufficient balance or account not found are handled explicitly.
     *
     * @param txn the transaction to be executed
     * @return Mono emitting the executed transaction with updated status, wrapped in a transaction
     */
    public Mono<Txn> executeTxn(Txn txn) {
        return updateBalances(txn)
                .then(txnTemplate.findAndUpdateStatusById(txn.getId(), TxnStatus.SUCCESS))
                .onErrorResume(DataIntegrityViolationException.class, e -> handleTxnError(txn, TxnStatus.FAILED, ErrorReason.INSUFFICIENT_BALANCE))
                .onErrorResume(AccountNotFoundException.class, e -> handleTxnError(txn, TxnStatus.FAILED, ErrorReason.ACCOUNT_NOT_FOUND))
                .as(transactionalOperator::transactional);
    }

    /**
     * Updates account balances based on transaction entries. Uses concatMap to ensure sequential balance updates.
     * If no account is found, an error is emitted to indicate the problem.
     *
     * @param txn the transaction containing entries for balance updates
     * @return Flux emitting counts of updated balances or an error if an account is not found
     */
    public Flux<Long> updateBalances(Txn txn) {
        return Flux.fromIterable(txn.getEntries())
                .concatMap(entry -> accountRepository.findAndIncrementBalanceByAccountNum(entry.getAccountNum(), entry.getAmount()))
                .handle((updatedCount, sink) -> {
                    if (updatedCount < 1) {
                        sink.error(new AccountNotFoundException("Account not found for update"));
                    } else {
                        sink.next(updatedCount);
                    }
                });
    }

    /**
     * Helper method to handle transaction errors and set appropriate status and error reason.
     *
     * @param txn          the transaction that encountered an error
     * @param status       the status to set for the failed transaction
     * @param errorReason  the reason for the error
     * @return Mono emitting an error with a custom TransactionException
     */
    private Mono<Txn> handleTxnError(Txn txn, TxnStatus status, ErrorReason errorReason) {
        txn.setStatus(status);
        txn.setErrorReason(errorReason);
        return Mono.error(new TransactionException(txn));
    }
}
