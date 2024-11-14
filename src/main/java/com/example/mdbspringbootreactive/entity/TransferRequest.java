package com.example.mdbspringbootreactive.entity;

/**
 * Represents a transfer request in a banking or financial system, containing 
 * details of the target account and the amount to be transferred. This class 
 * is immutable, ensuring the safety and integrity of transfer data.
 */
public class TransferRequest {

    private final String to;
    private final double amount;

    /**
     * Constructs a new TransferRequest with the specified target account and amount.
     *
     * @param to     the target account number where funds are to be transferred. 
     *               Must not be null or empty.
     * @param amount the amount to transfer. Must be a positive value.
     * @throws IllegalArgumentException if the target account is null or empty, or if 
     *                                  the amount is not positive.
     */
    public TransferRequest(String to, double amount) {
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("Target account must not be null or empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.to = to;
        this.amount = amount;
    }

    /**
     * Returns the target account number for this transfer request.
     *
     * @return the target account number.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the amount specified for this transfer request.
     *
     * @return the transfer amount.
     */
    public double getAmount() {
        return amount;
    }
}

