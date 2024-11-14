package com.example.mdbspringbootreactive.template;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;

import com.example.mdbspringbootreactive.enumeration.ErrorReason;
import com.example.mdbspringbootreactive.enumeration.TxnStatus;
import com.example.mdbspringbootreactive.model.Txn;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.query.Criteria;


/**
 * Service for managing transaction operations in a reactive MongoDB environment.
 * Provides methods to save transactions and update their status with optional error reasons.
 */
@Service
public class TxnTemplate {

    private final ReactiveMongoTemplate template;

    /**
     * Constructs a TxnTemplate with the provided ReactiveMongoTemplate.
     *
     * @param template the ReactiveMongoTemplate for MongoDB operations
     */
    public TxnTemplate(ReactiveMongoTemplate template) {
        this.template = template;
    }

    /**
     * Saves a transaction entity to the database.
     *
     * @param txn the transaction to be saved
     * @return Mono emitting the saved transaction
     */
    public Mono<Txn> save(Txn txn) {
        return template.save(txn);
    }

    /**
     * Updates the status of a transaction by ID.
     *
     * @param id     the ID of the transaction to update
     * @param status the new status to set
     * @return Mono emitting the updated transaction, or empty if not found
     */
    public Mono<Txn> findAndUpdateStatusById(String id, TxnStatus status) {
        return findAndUpdateStatusById(id, status, null);
    }

    /**
     * Updates the status and error reason of a transaction by ID.
     *
     * @param id          the ID of the transaction to update
     * @param status      the new status to set
     * @param errorReason the error reason to set (optional)
     * @return Mono emitting the updated transaction, or empty if not found
     */
    public Mono<Txn> findAndUpdateStatusById(String id, TxnStatus status, ErrorReason errorReason) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = buildUpdate(status, errorReason);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return template.findAndModify(query, update, options, Txn.class);
    }

    /**
     * Builds an update object based on the provided status and optional error reason.
     *
     * @param status      the transaction status to set
     * @param errorReason the error reason to set (if any)
     * @return the Update object for the transaction
     */
    private Update buildUpdate(TxnStatus status, ErrorReason errorReason) {
        Update update = new Update().set("status", status);
        if (errorReason != null) {
            update.set("errorReason", errorReason);
        }
        return update;
    }
}