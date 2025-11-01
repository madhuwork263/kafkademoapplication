package com.example.demo.repository;

import com.example.demo.model.CanonicalTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanonicalTransactionRepository extends MongoRepository<CanonicalTransaction, String> {
}
