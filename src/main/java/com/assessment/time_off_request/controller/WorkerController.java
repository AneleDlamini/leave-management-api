/* Handles requests sent to and from Worker client */

package com.assessment.time_off_request.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.service.WorkerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "Worker Management", description = "Endpoints to manage workers")
@RestController
@RequestMapping("api/user")
public class WorkerController {

    private final Worker worker;

    // Create WorkerService instance
    @Autowired
    WorkerService service;

    WorkerController(Worker worker) {
        this.worker = worker;
    }

    // ----------------- ADD worker -----------------
    @Operation(
            summary = "Add a new worker", 
            description = "Creates a new worker in the system with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Worker created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public void addWorker(@RequestBody Worker worker) {   
        try {
            service.addWorker(worker);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid worker: " + worker);
        }
        
    }
    

    // ----------------- GET all workers -----------------
    @Operation(summary = "Get all workers", description = "Retrieve all workers stored")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all workers"),
        @ApiResponse(responseCode = "404", description = "No workers found")
    })
    @GetMapping("/all")
    public List<Worker> getAllWorkers(){
        List<Worker> workers = null;
        try {
            workers = service.getWorkers();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No workers found");
        }

        return workers;
    }

    // ----------------- GET worker vacation credits -----------------
     @Operation(summary = "Get credits of a worker by ID", description = "Returns the current vacation days balance for a specific worker by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Worker credits retrieved"),
        @ApiResponse(responseCode = "404", description = "Worker not found")
    })
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/credits")
    public long getCredits(
        @Parameter(
            description = "Email of the worker",
            example = "johnsmith@gmail.com",
            required = true
        )
        
        String email){

        long balance = 0;

        try {

            balance = service.getCurrentCredits(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No worker found");
        }
        return balance;
    }


}
