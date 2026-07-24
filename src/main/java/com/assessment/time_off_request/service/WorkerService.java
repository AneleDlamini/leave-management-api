/* Logic for all Worker operations */

package com.assessment.time_off_request.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.repo.RequestRepo;
import com.assessment.time_off_request.repo.WorkerRepo;

import jakarta.persistence.EntityNotFoundException;

@Service 
public class WorkerService {

    // Retrieve repo
    @Autowired
    WorkerRepo workerRepo;

    @Autowired
    RequestRepo requestRepo;

    // Get all Workers in database
    public List<Worker> getWorkers(){
        return workerRepo.findAll(); 
    }

    // Get Worker from database by email
     public Worker getWorkerData(String email){
        return workerRepo.findByEmail(email);
    }

    // Add a Worker to databse
    public void addWorker(Worker worker){
        workerRepo.save(worker);
    }

    // View remaining vacation days for Worker
    public long getCurrentCredits(String email){
        return workerRepo.findVacationCreditsByEmail(email); 
    }
    

    // Update Workers vacation credits/balance - only once approved
    public void updateRemainingCredits(int requestid){
        Request queriedRequest = requestRepo.findById(requestid).orElseThrow(() -> new EntityNotFoundException("Request not found"));
        int workerid = queriedRequest.getWorkerID(); // getting workerid from request
        Worker queriedWorker = workerRepo.findById(workerid)
        .orElseThrow(() -> new EntityNotFoundException("Worker not found"));

        long credits = queriedWorker.getVacationCredits() - queriedRequest.getRequestedDays();

        // Worker queriedWorker = workerRepo.findById(workerid).orElseThrow(() -> new EntityNotFoundException("Worker not found"));
        queriedWorker.setVacationCredits(credits); // updating field object of queried worker
        workerRepo.save(queriedWorker); // updating/saving queried worker in database
    }

    // Can a user make a request based on remaining credits?
    public boolean hasSufficientCredits(String email, int workerid){
        long credits = getCurrentCredits(email);
        if(credits > 0 && credits > requestRepo.getRequestedDaysById(workerid)){
            return true;
        }
        return false;
    }



}
