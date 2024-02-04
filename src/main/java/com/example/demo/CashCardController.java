package com.example.demo;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.PutMapping;


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
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) { /*
                                                                                 * makes Spring Web aware of the
                                                                                 * requestedId supplied in the HTTP
                                                                                 * request and the principal (the current user authenticated
                                                                                 * and authorized information) 
                                                                                 */
        CashCard cashCard = findCashCard(requestedId, principal);//Added principal to get access to current username provided from BasicAuth
        if (cashCard !=null) {
            return ResponseEntity.ok(cashCard);
        } else
            return ResponseEntity.notFound().build();
    }

    /*
    * Saving the new CashCard and return its location. 
    */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal) {

        CashCard cashCardWithOwner =  new CashCard(null, cashCard.getAmount(), principal.getName());
        
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        /* Constructing a URI to the newly created CashCard */
        URI cashCardLocation = ucb.path("/cashcards/{id}").buildAndExpand(savedCashCard.getId()).toUri();

        return ResponseEntity.created(cashCardLocation).build();
    }
    /**
     * 
     * @return a list of CashCards objects sorted ascending by amount
     * 
     */
    @GetMapping()
    private ResponseEntity<List<CashCard>> getAllCashCards(Pageable pageable, Principal principal) {

        /*
         * getSortOr() method provides default values for the page, size, and sort parameters
         * Spring provides the default page and size values (they are 0 and 20, respectively)
         */
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
        return ResponseEntity.ok(page.getContent());
    }
    /**
     * 
     * @param requestedId
     * @param cashCardUpdate
     * @param principal
     * @return  udpdate a CashCard with the specified amount
     */
    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        
        /*
        *Retrieving the CashCard to the submitted requestedId and Principal (provided by Spring Security) to ensure only the authenticated, 
        *authorized owner may update his CashCard
        */
        CashCard cashCard = findCashCard(requestedId, principal);

        if(cashCard != null && principal.getName().equals(cashCard.getOwner())){  
            CashCard updatedCashCard = new CashCard(cashCard.getId(), cashCardUpdate.getAmount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            // just return 204 NO CONTENT for now.
            return ResponseEntity.noContent().build();
        }
            return ResponseEntity.notFound().build();
    }

    private CashCard findCashCard(Long requestedId, Principal principal){
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long requestedId, Principal principal){
        
        if(!cashCardRepository.existsByIdAndOwner(requestedId, principal.getName())){
            return ResponseEntity.notFound().build();
        }
        cashCardRepository.deleteById(requestedId); 
        return ResponseEntity.noContent().build(); 
    }
}
