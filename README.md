# mdb-spring-boot-reactive

# Reactive Java Spring Boot with Spring Data MongoDB

## About

A simplified cash balance application built with reactive Java Spring Boot and Spring Data MongoDB. This project showcases:

- **Create, Read, and Update** operations with `ReactiveMongoRepository`
- **Create, Read, and Update** operations with `ReactiveMongoTemplate`
- Wrapping queries in a **multi-document transaction**

## Supported Versions

- **Java**: 17
- **Spring Boot Starter Webflux**: 3.2.3
- **Spring Boot Starter Reactive Data Mongodb**: 3.2.3

## How It Works

A bank account can be created with a unique `accountNum` and starts with a balance of `$0`. The operations include:

1. **Create Account**: Creates a new account with balance `$0`.
2. **Debit/Credit Operation**: Updates the account balance.
3. **Transfer Operation**: Transfers funds between two accounts.

Successful transactions are saved in the `transactions` collection with status `PENDING` and later updated to `SUCCESS`.
