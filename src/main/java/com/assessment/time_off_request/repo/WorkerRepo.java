/* Worker repository to communicate with Worker db */
package com.assessment.time_off_request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.assessment.time_off_request.model.Worker;
import java.util.List;


@Repository
public interface WorkerRepo extends JpaRepository<Worker, Integer>{

    // Find worker by WorkerID
    List<Worker> findByWorkerID(int workerID);

    // Find worker by email
    Worker findByEmail(String email);

    // Find Vacation Credits by WorkerID
    @Query("SELECT w.vacationCredits FROM Worker w WHERE w.email = :email")
    long findVacationCreditsByEmail(String email);
    

}
