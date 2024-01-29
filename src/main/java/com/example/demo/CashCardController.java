package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController/*Spring component capable of handling HTTP requests*/
@RequestMapping("/cashcards")/*Indicates which address requests must have to access this Controller*/
public class CashCardController {

    @GetMapping("/{requestedId}")/*marks a method as a handler method. GET requests that match cashcards/{requestedID} will be handled by this method*/
    private ResponseEntity<CashCard> findById (@PathVariable Long requestedId){ /*makes Spring Web aware of the requestedId supplied in the HTTP request*/
        if(requestedId.equals(99L)){
            CashCard cashCard = new CashCard(99L,123.45);
            return ResponseEntity.ok(cashCard);
        }
        else
            return ResponseEntity.notFound().build();
    }

}
