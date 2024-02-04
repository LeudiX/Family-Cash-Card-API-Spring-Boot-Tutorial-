package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long>{

    CashCard findByIdAndOwner(Long id, String owner); //Filtering data access by CashCard owner

    Page<CashCard> findByOwner(String owner, Pageable pageable); //Filtering data access by CashCard owner

    boolean existsByIdAndOwner(Long id, String owner); //Checking the existence of a CasChard. Obtaining just the necessary information about the CashCard's existential status
}

/*
*NOTE: Spring Data will take care of the actual implementations (writing the SQL queries)
 */