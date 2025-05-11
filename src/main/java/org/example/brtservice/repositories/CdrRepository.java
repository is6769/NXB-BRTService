package org.example.brtservice.repositories;

import org.example.brtservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CdrRepository extends JpaRepository<Cdr,Long> {
}
