package com.udea.bancoudea.controller;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionFacade;

    public TransactionController(TransactionService transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    //Obtener todas transacciones por numero de cuenta
    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForAccount(@PathVariable String accountNumber){
        return ResponseEntity.ok(transactionFacade.getTransactionsForAccount(accountNumber));
    }

    //Tranferir plata
    @PostMapping
    public ResponseEntity<TransactionDTO> TransferMoney(@RequestBody TransactionDTO transactionDTO){
        if (transactionDTO.getAmount() < 1) {
            throw new IllegalArgumentException("transfer is invalid");
        }
        return ResponseEntity.ok(transactionFacade.TransferMoney(transactionDTO));
    }
}
