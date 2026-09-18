# Banking Transaction Processor

A Spring Boot REST API for managing bank accounts and processing transactions.

## Features

- Create unique bank accounts
- Deposit money
- Withdraw money
- Transfer money between accounts
- Retrieve account balance
- Retrieve transaction history
- Validate transaction amounts
- Prevent overdrafts
- Handle invalid or missing accounts
- Maintain transaction timestamps and transaction IDs

## Technology Stack

- Java 21
- Spring Boot 4.1.1
- Maven
- JUnit 5
- Mockito
- Spring MockMvc

## Architecture

The application follows a simple layered architecture:

```text
Client
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
In-Memory Storage
