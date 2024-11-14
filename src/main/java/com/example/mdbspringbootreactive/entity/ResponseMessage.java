package com.example.mdbspringbootreactive.entity;

/**
 * A simple, immutable class for holding a response message.
 *
 * @param message the message content to be sent in the response
 */
public record ResponseMessage(String message) {

}