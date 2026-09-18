package com.bankingapp.exception;

public class InvalidAmountException extends RuntimeException{
    public InvalidAmountException(){
        super("Transaction amount is invalid must be greater than 0");
    }
}
