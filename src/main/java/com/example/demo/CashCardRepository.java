package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long>{

    CashCard findByIdAndOwner(Long id, String owner); //Filtering by owner when i need to access a single CashCard

    Page<CashCard> findByOwner(String owner, Pageable pageable); //Filtering by owner when i need to recover a list of CashCards
}
