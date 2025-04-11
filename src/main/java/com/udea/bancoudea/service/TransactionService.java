package com.udea.bancoudea.service;

import com.udea.bancoudea.DTO.TransactionDTO;
import com.udea.bancoudea.entity.Customer;
import com.udea.bancoudea.entity.Transaction;
import com.udea.bancoudea.repository.CustomerRepository;
import com.udea.bancoudea.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public TransactionDTO TransferMoney(TransactionDTO transactionDTO){
        //validar que los numeros de cuentas no sean nulos
        if(transactionDTO.getSenderAccountNumber()==null || transactionDTO.getReceiverAccountNumber()==null){
            throw new IllegalArgumentException("Sender and Receiver account numbers cannot be null");
        }

        //Buscar los clientes por numero de cuenta
        Customer sender = customerRepository.findByAccountNumber(transactionDTO.getSenderAccountNumber())
                .orElseThrow(()-> new IllegalArgumentException("Sender Account Number not found"));

        Customer receiver = customerRepository.findByAccountNumber(transactionDTO.getReceiverAccountNumber())
                .orElseThrow(()-> new IllegalArgumentException("Receiver Account Number not found"));

        //Validar que el remitente tenga saldo suficiente
        if(sender.getBalance()<transactionDTO.getAmount()){
            throw new IllegalArgumentException("Sender balance not enough");
        }

        //Realiza la transferencia
        sender.setBalance(sender.getBalance()-transactionDTO.getAmount());
        receiver.setBalance(receiver.getBalance()+transactionDTO.getAmount());

        //Guardar los cambios en las cuentas
        customerRepository.save(sender);
        customerRepository.save(receiver);

        //Fecha de tranferencia
        LocalDateTime timestamp = LocalDateTime.now();

        //Crear y Guardar la transaccion
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(sender.getAccountNumber());
        transaction.setReceiverAccountNumber(receiver.getAccountNumber());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTimestamp(timestamp);
        transaction = transactionRepository.save(transaction);

        //Devolver la transaccion creada como un DTO
        TransactionDTO savedTransaction = new TransactionDTO();
        savedTransaction.setId(transaction.getId());
        savedTransaction.setSenderAccountNumber(sender.getAccountNumber());
        savedTransaction.setReceiverAccountNumber(receiver.getAccountNumber());
        savedTransaction.setAmount(transactionDTO.getAmount());
        savedTransaction.setTimestamp(timestamp);
        return savedTransaction;
    }

    public List<TransactionDTO> getTransactionsForAccount(String accountNumber){
        List<Transaction> transactions = transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber,accountNumber);
        return transactions.stream().map(transaction -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(transaction.getId());
            dto.setSenderAccountNumber(transaction.getSenderAccountNumber());
            dto.setReceiverAccountNumber(transaction.getReceiverAccountNumber());
            dto.setAmount(transaction.getAmount());
            dto.setTimestamp(transaction.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}
