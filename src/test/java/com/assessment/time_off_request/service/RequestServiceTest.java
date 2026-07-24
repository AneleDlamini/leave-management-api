package com.assessment.time_off_request.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.model.Request.requestStatus;
import com.assessment.time_off_request.repo.RequestRepo;
import com.assessment.time_off_request.repo.WorkerRepo;

import jakarta.persistence.EntityNotFoundException;

public class RequestServiceTest {

    @Mock
    private WorkerRepo workerRepo;

    @Mock
    private RequestRepo requestRepo;

    @InjectMocks
    private RequestService requestService;

    @Mock
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // 1. Request successfully created - if vacation credits are < 30
    @Test
    void testCreateRequest() {
        // Arrange (define behavior of mock)
        Request request = new Request();
        request.setWorkerID(1);
        request.setStartDate("23/05/2025");
        request.setEndDate("25/05/2025");
        request.setRequestedDays();
        request.setAssignedManager("johnsmith@gmail.com");
        request.setCreatedAt();

        when(workerService.getCurrentCredits(new Worker(1).getEmail())).thenReturn((long) 5);

        when(requestRepo.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (call service method)
        requestService.createRequest(request);

        // Assert (verify result & interaction)
        verify(requestRepo).save(request);
    }

    // 2. Create request  - if vacation credits are exhausted
    @Test
    void testCreateRequestWithNoCredits() {
        Request request = new Request();
        request.setWorkerID(1);
        request.setStartDate("23/05/2025");
        request.setEndDate("25/05/2025");
        request.setRequestedDays();
        request.setAssignedManager("johnsmith@gmail.com");
        request.setCreatedAt();

        when(workerService.getCurrentCredits(new Worker(1).getEmail())).thenReturn((long) 0);

        assertThrows(IllegalStateException.class, () -> requestService.createRequest(request));

        verify(requestRepo, never()).save(any(Request.class));
    }



    // 3. Invalid request or duplicate
    @Test
    void testCreateNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(null));
    }

    // 4. Return all requests successfully
    @Test
    void testGetAllRequests() {
        List<Request> requests = List.of(new Request(), new Request());
        when(requestRepo.findAll()).thenReturn(requests);

        List<Request> result = requestService.getAllRequests();

        assertEquals(2, result.size());
        verify(requestRepo).findAll();
    }

    // 5. Returns empty list/repository fails
    @Test
    void testGetAllRequestsFromEmptyRepo() {
        when(requestRepo.findAll()).thenReturn(Collections.emptyList());

        List<Request> result = requestService.getAllRequests();

        assertTrue(result.isEmpty());
        verify(requestRepo).findAll();
    }

    // 6. Return all requests under one user
    @Test
    void testGetAllUserRequests() {
        int workerId = 1;
        List<Request> requests = List.of(new Request(), new Request());
        when(requestRepo.findByWorkerID(workerId)).thenReturn(requests);

        List<Request> result = requestService.getAllUserRequests(workerId);

        assertEquals(2, result.size());
        verify(requestRepo).findByWorkerID(workerId);
    }

    // 7. Return empty list if there's none
    @Test
    void testGetAllUserRequestsFromEmptyRepo() {
        int workerId = 1;
        when(requestRepo.findByWorkerID(workerId)).thenReturn(Collections.emptyList());

        List<Request> result = requestService.getAllUserRequests(workerId);

        assertTrue(result.isEmpty());
        verify(requestRepo).findByWorkerID(workerId);
    }

    // 8. Returns single request by ID
    @Test
    void testGetUserRequest() {
        int requestId = 1;
        Request request = new Request();
        request.setRequestID(requestId);

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));

        Request result = requestService.getUserRequest(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getRequestID());
        verify(requestRepo).findById(requestId);
    }

    // 9. Request not found
    @Test
    void testGetUserRequestNotFound() {
        int requestId = 99; 
       
        when(requestRepo.findById(requestId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> requestService.getUserRequest(requestId));

        assertEquals("User Request not found", exception.getMessage());

        verify(requestRepo).findById(requestId);
    }

    // 10. Successfully updates request status
    @Test
    void testProcessRequest() {
        int requestId = 1;
        Request request = new Request();
        request.setRequestID(requestId);

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepo.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(workerService).updateRemainingCredits(requestId);

        requestService.processRequest(requestId, true);

        assertEquals("APPROVED", request.getStatus().name());
        verify(requestRepo).save(request);
    }

    // 11. Invalid status input or request not found
    @Test
    void testProcessRequestUnsuccessful() {
        int requestId = 99;

        when(requestRepo.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.processRequest(requestId, true));
    }

    // 12. Filter requests by status - approved
    @Test
    void testGetRequestsByStatusApproved() {
        Request request = new Request();
        request.setRequestID(1);
        request.setStatus(requestStatus.APPROVED);

        Request request2 = new Request();
        request2.setRequestID(2);
        request2.setStatus(requestStatus.APPROVED);

        Request request3 = new Request();
        request3.setRequestID(3);
        request3.setStatus(requestStatus.PENDING);

        List<Request> approvedRequests = List.of(request, request2);

        when(requestRepo.findByStatus(requestStatus.APPROVED)).thenReturn(approvedRequests);

        List<Request> result = requestService.getRequestsByStatus(requestStatus.APPROVED);

        assertEquals(2, result.size());
        for (Request r : result) {
            assertEquals(requestStatus.APPROVED, r.getStatus());
        }

        verify(requestRepo).findByStatus(requestStatus.APPROVED);



        assertEquals(2, approvedRequests.size());

        for (Request r : approvedRequests) {
            assertEquals("APPROVED", r.getStatus().name());
        }

    }

    // 13. Filter requests by status - pending
    @Test
    void testGetRequestsByStatusPending() {
        Request request = new Request();
        request.setRequestID(1);
        request.setStatus(requestStatus.APPROVED);

        Request request2 = new Request();
        request2.setRequestID(2);
        request2.setStatus(requestStatus.APPROVED);

        Request request3 = new Request();
        request3.setRequestID(3);
        request3.setStatus(requestStatus.PENDING);

        List<Request> approvedRequests = List.of(request3);

        when(requestRepo.findByStatus(requestStatus.PENDING)).thenReturn(approvedRequests);

        List<Request> result = requestService.getRequestsByStatus(requestStatus.PENDING);

        assertEquals(1, result.size());
        for (Request r : result) {
            assertEquals(requestStatus.PENDING, r.getStatus());
        }

        verify(requestRepo).findByStatus(requestStatus.PENDING);



        assertEquals(1, approvedRequests.size());

        for (Request r : approvedRequests) {
            assertEquals("PENDING", r.getStatus().name());
        }
    }


}
