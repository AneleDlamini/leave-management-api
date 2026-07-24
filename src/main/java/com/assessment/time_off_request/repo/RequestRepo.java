/* Worker repository to communicate with Request db */
package com.assessment.time_off_request.repo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Request.requestStatus;

@Repository
public interface RequestRepo extends JpaRepository<Request, Integer>{

    // Find Requested Days by WorkerID
    @Query("SELECT r.requestedDays FROM Request r WHERE r.workerID = :id")
    long getRequestedDaysById(int id);

     // Filter requests by status
    List<Request> findByStatus(requestStatus status);

    // Find specific user requests
    List<Request> findByWorkerID(int workerID);

    // Find specific user request - single 
    Request findByRequestID (int requestID);
}
