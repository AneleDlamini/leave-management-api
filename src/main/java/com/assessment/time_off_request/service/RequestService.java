/* Business logic for vacation requests */
package com.assessment.time_off_request.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.model.Request.requestStatus;
import com.assessment.time_off_request.repo.RequestRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RequestService {

    @Autowired
    private WorkerService workerService;

    @Autowired
    RequestRepo repository;

    // Create a new Request
    public void createRequest(Request newRequest){
        if (newRequest == null) {
            throw new IllegalArgumentException("Request cannot be null");

        }

        if(new Worker(newRequest.getWorkerID()).getVacationCredits() == 0){
            throw new IllegalStateException("Not enough credits to make request");
        }
        
        repository.save(newRequest);
    }

    // View All Requests
    public List<Request> getAllRequests(){
        return repository.findAll();
    }

    // View Requests by workerId
    public List<Request> getAllUserRequests(int id){
        return repository.findByWorkerID(id);
    }

    // View Single Request by workerId
    public Request getUserRequest(int id){
        return repository.findById(id)
                      .orElseThrow(() -> new EntityNotFoundException("User Request not found"));
    }

    // Process a request - change status, update vaction days if approved
    public Request processRequest(int requestid, boolean approved){
        try {

            // get the request
            Request request = repository.findById(requestid)
                    .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestid));

            // has the request been processed?
            if (request.getStatus() != requestStatus.PENDING) {
                throw new IllegalStateException("Request has already been processed");
            }

            // if not, update status accordingly
            if (approved) {
                // if approved, change status and update remaining credits
                request.setStatus(requestStatus.APPROVED);
                workerService.updateRemainingCredits(requestid);

            } else {
                request.setStatus(requestStatus.REJECTED);
            }

            return repository.save(request); // store update

        } catch (Exception e) {
            throw new EntityNotFoundException("Failed to process request: " + e.getMessage(), e);
        }

    }

    // Filter by status
    public List <Request> getRequestsByStatus(requestStatus status){
        return repository.findByStatus(status);
    } 

}
