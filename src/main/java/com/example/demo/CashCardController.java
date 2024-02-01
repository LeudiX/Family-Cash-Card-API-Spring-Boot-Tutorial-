package com.example.demo;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController /* Spring component capable of handling HTTP requests */
@RequestMapping("/cashcards") /* Indicates which address requests must have to access this Controller */
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}") /*
                                   * marks a method as a handler method. GET requests that match
                                   * cashcards/{requestedID} will be handled by this method
                                   */
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) { /*
                                                                                 * makes Spring Web aware of the
                                                                                 * requestedId supplied in the HTTP
                                                                                 * request
                                                                                 */
        Optional<CashCard> cashCard = cashCardRepository.findById(requestedId);
        if (cashCard.isPresent()) {
            return ResponseEntity.ok(cashCard.get());
        } else
            return ResponseEntity.notFound().build();
    }

    /* Saving the new CashCard and return its location. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb) {

        CashCard savedCashCard = cashCardRepository.save(cashCard);
        /* Constructing a URI to the newly created CashCard */
        URI cashCardLocation = ucb.path("/cashcards/{id}").buildAndExpand(savedCashCard.getId()).toUri();

        return ResponseEntity.created(cashCardLocation).build();
    }

}
