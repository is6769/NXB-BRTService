package org.example.brtservice.repositories;

import org.example.brtservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CdrRepository extends JpaRepository<Cdr,Long> {


    @Query(value = "select * from cdrs where consumed_status='NEW' limit 10",nativeQuery = true)
    List<Cdr> findFirst10NonConsumedRecords();


    @Query(value = "select COUNT(*) from cdrs where consumed_status='NEW'",nativeQuery = true)
    Integer findNumberOfNonConsumedRows();
    //@Query("select * from Cdr c")
    //void findFirst10SortedWithDateTime();
}
