/* Handles prompts that utilize or alter vacation requests */
package com.assessment.time_off_request.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Request.requestStatus;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.service.RequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Request Management", description = "Endpoints to create, retrieve, and update requests")
@RestController
@RequestMapping("api/request")
public class RequestController {

    @Autowired
    RequestService service;

    // ----------------- GET all requests -----------------
    /**
     * @return
     */
    @Operation(
        summary = "Get all requests",
        description = "Returns all requests in the system.\nAccess: MANAGER only"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all requests returned"),
        @ApiResponse(responseCode = "403", description = "Access forbidden for this role")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/all")
    public List<Request> getAllRequests() {

        List<Request> allRequests = null;

        try {
            allRequests = service.getAllRequests();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised for this service");
        }
        return allRequests;
    }

    // ----------------- PROCESS a request -----------------
    @Operation(
        summary = "Process a request",
        description = """
            Approves or rejects a request.
            Access: MANAGER only.
            Business rules:
            1. Status must be 'pending' to process.
            2. 'action' must be either 'approve' or 'reject'.
        """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of updated requests returned"),
        @ApiResponse(responseCode = "403", description = "Access forbidden for this role")
    })
    @GetMapping("/{id}/process")
    public ResponseEntity <Request> processRequest (@PathVariable int id ,@RequestParam boolean approved) {

        // Checking if the logged in user is the same as the assigned manager for request - thourization
        Request updatedRequest = null;

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String loginEmail = authentication.getName();
            Request queriedRequest = service.getUserRequest(id);

            if(queriedRequest.getAssignedManager().equals(loginEmail)){
                updatedRequest = service.processRequest(id, approved);
                
        }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised for this service");
        }

        return ResponseEntity.ok(updatedRequest);

        
    }

     // ----------------- FILTER requests by status -----------------
    @Operation(
        summary = "Filter vacation requests by status - [APPROVED, PENDING, REJECTED]",
        description = "Retrieve requests filtered by status.\nAccess: ALL USERS (WORKER or MANAGER)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of requests returned"),
        @ApiResponse(responseCode = "403", description = "Access forbidden for this role"),
        @ApiResponse(responseCode = "400", description = "Invalid status requested")
    })
    @PreAuthorize("hasRole('WORKER') or hasRole('MANAGER')")
    @GetMapping("/status")
    public ResponseEntity <List<Request>> getRequestByStatus (@RequestParam String status) {
        requestStatus reqStatus ;
        try {
            reqStatus = requestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised for this service");
        }
        List<Request> filteredRequests = service.getRequestsByStatus(reqStatus);
        System.out.println(filteredRequests);
        return ResponseEntity.ok(filteredRequests);
        }


}
