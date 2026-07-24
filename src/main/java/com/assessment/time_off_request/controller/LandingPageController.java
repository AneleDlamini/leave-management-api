/* Defining lading pages based on user role */
package com.assessment.time_off_request.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.service.RequestService;
import com.assessment.time_off_request.service.WorkerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Landing Page", description = "Operations redirecting landing page based on roles")
@RestController
@RequestMapping("/home")
public class LandingPageController {

    // Define service of class
    @Autowired
    RequestService requestService;

    @Autowired
    WorkerService workerService;

    // ----------------- GET data for Manager landing page -----------------
    @Operation(
        summary = "Get Manager landing page",
        description = "Retrieves the landing page data specifically designed for managers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Manager landing page data retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden for this role"),
        @ApiResponse(responseCode = "404", description = "Manager landing page data not found")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager")
    public List<Request> getManagerLandingPage() {
        List<Request> mangerLanding = null;

        try {
            mangerLanding = requestService.getAllRequests();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not retrieve data");
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised for this service");
        }

        return mangerLanding;
    }

     // ----------------- GET data for Worker landing page -----------------
    @Operation(
        summary = "Get normal user/worker landing page",
        description = "Retrieves the landing page data specifically designed for normal users")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User landing page data retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden for this role"),
        @ApiResponse(responseCode = "404", description = "User landing page data not found")
    })
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/user")
    public List<Request> getUserLandingPage() {
        List<Request> data;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // logged in user's email

            // Use email to extract workerID from Worker
            Worker worker = workerService.getWorkerData(email);
            int workerID = worker.getWorkerID();


            // Fetch data from another DB based on the logged in user
            data = requestService.getAllUserRequests(workerID);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not retrieve data");
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorised for this service");
        }
        

        return data; 
    }
}
