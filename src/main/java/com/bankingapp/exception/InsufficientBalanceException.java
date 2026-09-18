package com.bankingapp.exception;

public class InsufficientBalanceException extends RuntimeException{

    public InsufficientBalanceException(){
        super("insufficient balance");
    }

}
