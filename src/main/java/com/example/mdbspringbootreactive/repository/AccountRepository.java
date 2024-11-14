package com.example.mdbspringbootreactive.repository;

import com.example.mdbspringbootreactive.model.Account;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Mono;

/**
 * Repository interface for performing reactive, non-blocking CRUD operations on {@link Account} documents
 * in MongoDB. Extends {@link ReactiveMongoRepository} for basic CRUD operations and includes custom query
 * methods for account-specific operations.
 */
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    
    /**
     * Retrieves an {@link Account} by its account number.
     * Uses a MongoDB query to find the account based on the provided account number.
     *
     * @param accountNum the account number of the {@link Account} to retrieve.
     * @return a {@link Mono} emitting the {@link Account} if found, or empty if not found.
     */
    @Query("{accountNum:'?0'}")
    Mono<Account> findByAccountNum(String accountNum);

    /**
     * Increments the balance of an {@link Account} by a specified amount based on the account number.
     *
     * @param accountNum the account number of the {@link Account} to update.
     * @param increment the amount to add to the account balance.
     * @return a {@link Mono} emitting the number of updated documents as a {@link Long}.
     */
    @Update("{'$inc':{'balance': ?1}}")
    Mono<Long> findAndIncrementBalanceByAccountNum(String accountNum, double increment);

}
