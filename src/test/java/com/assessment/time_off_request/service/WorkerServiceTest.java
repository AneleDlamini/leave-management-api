package com.assessment.time_off_request.service;

/* Testnig business logic for Worker Service */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.repo.RequestRepo;
import com.assessment.time_off_request.repo.WorkerRepo;
import com.assessment.time_off_request.service.WorkerService;

public class WorkerServiceTest {

    @Mock
    private WorkerRepo workerRepo;

    @Mock
    private RequestRepo requestRepo;

    @InjectMocks
    private WorkerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Worker worker;

    // 1. Add Worker 
    @Test
    void testAddWorker() {
        worker = new Worker();
        worker.setWorkerID(1);
        worker.setFirstName("Jane");
        worker.setLastName("Doe");
        worker.setEmail("janedoe@gmail.com");
        worker.setVacationCredits(15);
        worker.setRole("WORKER");
        //worker.setPasswordHash(passwordEncoder.encode("janed@123"));

        // Stub the save method to return the worker
        when(workerRepo.save(worker)).thenReturn(worker);

        // Stub findByEmail to return the worker
        when(workerRepo.findByEmail("janedoe@gmail.com")).thenReturn(worker);

        service.addWorker(worker);
        Worker resultWorker = service.getWorkerData(worker.getEmail());
        assertNotNull(resultWorker);
        assertEquals("janedoe@gmail.com", resultWorker.getEmail());
        System.out.println("testAddWorker passed: " + resultWorker.getEmail() + " with credits " + resultWorker.getVacationCredits());
    }

    // 2. Add Null worker
    @Test
    void testAddNullWorker() {
        when(workerRepo.save(null)).thenThrow(new IllegalArgumentException("Worker cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> service.addWorker(null));
    }

    // 3. Get all workers
    @Test
    void testGetWorkers() {
        worker = new Worker();
        worker.setWorkerID(1);
        worker.setFirstName("Jane");
        worker.setLastName("Doe");
        worker.setEmail("janedoe@gmail.com");
        worker.setVacationCredits(15);
        worker.setRole("WORKER");

        List<Worker> workers = new ArrayList<>();
        workers.add(worker);

        Worker worker2 = new Worker();
        worker2.setWorkerID(2);
        worker2.setFirstName("John");
        worker2.setLastName("Smith");
        worker2.setEmail("johnsmith@gmail.com");
        worker2.setVacationCredits(10);
        worker2.setRole("WORKER");

        workers.add(worker2);

        when(workerRepo.findAll()).thenReturn(workers);

        List<Worker> resultWorker = service.getWorkers();

        assertEquals(2, resultWorker.size());
        assertEquals("John", resultWorker.get(1).getFirstName());

        verify(workerRepo).findAll();
    }

    // 4. Get workers when there are no workers stored
    @Test
    void testGetWorkersFromEmptyList() {
        when(workerRepo.findAll()).thenReturn(Collections.emptyList());

        List<Worker> result = service.getWorkers();

        assertTrue(result.isEmpty());
    }


    // 5. Get worker by email
    @Test
    void testGetWorkerData() {
        worker = new Worker();
        worker.setWorkerID(1);
        worker.setFirstName("Jane");
        worker.setLastName("Doe");
        worker.setEmail("janedoe@gmail.com");
        worker.setVacationCredits(15);
        worker.setRole("WORKER");
        
        when(workerRepo.findByEmail("janedoe@gmail.com")).thenReturn(worker);

        Worker found = service.getWorkerData("janedoe@gmail.com");

        assertNotNull(found);
        assertEquals("janedoe@gmail.com", found.getEmail());

        verify(workerRepo).findByEmail("janedoe@gmail.com");
    }

    // 6. Request worker data when worker doesn't exist
    @Test
    void testGetNonExistentWorkerData() {
        when(workerRepo.findByEmail("bobjohnson@gmail.com")).thenReturn(null);

        Worker found = service.getWorkerData("bobjohnson@gmail.com");

        assertNull(found);  // Expect null if not found
    }

    // 7. Get remaining vacation days
    @Test
    void testGetCurrentCredits() {
        worker = new Worker();
        worker.setWorkerID(3);
        worker.setFirstName("Bob");
        worker.setLastName("Johnson");
        worker.setEmail("bobjohnson@gmail.com");
        worker.setVacationCredits(12);
        worker.setRole("MANAGER");

        when(workerRepo.findVacationCreditsByEmail("bobjohnson@gmail.com")).thenReturn(worker.getVacationCredits());

        long credits = service.getCurrentCredits("bobjohnson@gmail.com");

        assertEquals(12, credits);

        verify(workerRepo).findVacationCreditsByEmail("bobjohnson@gmail.com");
    }

    // 7. Retrieving vacation credentials when worker doesn't exist
    @Test
    void testGetCurrentCreditsForNonExistentWorker() {
        when(workerRepo.findByEmail("bobjohnson@gmail.com")).thenReturn(null);

        Worker resultWorker = service.getWorkerData("bobjohnson@gmail.com");
        assertNull(resultWorker);
    }


    // 8. Deduct vacation days and update 
    @Test
    void testUpdateRemainingCredits() {

      int requestId = 1;
      int workerId = 5;

      Request request = new Request();
      request.setRequestID(requestId);
      request.setWorkerID(workerId);
      request.setStartDate("23/05/2025");
      request.setEndDate("25/05/2025");
      request.setRequestedDays();

      Worker worker = new Worker();
      worker.setWorkerID(workerId);
      worker.setEmail("johnsmith@gmail.com");
      worker.setVacationCredits(20);

      when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
      //when(workerRepo.findVacationCreditsByEmail("johnsmith@impact.com")).thenReturn((long) 20);
      when(workerRepo.findById(workerId)).thenReturn(Optional.of(worker));
      when(workerRepo.save(any(Worker.class))).thenAnswer(invocation -> invocation.getArgument(0));

      service.updateRemainingCredits(requestId);

      assertEquals(18, worker.getVacationCredits()); // 20 - 2
      verify(workerRepo).save(worker); 
    }

    // 9. Verify sufficient Credits to make request

    @Test
    void testHasSufficientCreditsTrue() {
      Worker worker = new Worker();
      worker.setWorkerID(5);
      worker.setEmail("johnsmith@gmail.com");
      worker.setVacationCredits(20);

      when(workerRepo.findVacationCreditsByEmail("johnsmith@gmail.com")).thenReturn((long) 20);
      when(requestRepo.getRequestedDaysById(5)).thenReturn((long) 5);


      boolean result = service.hasSufficientCredits("johnsmith@gmail.com", 5);

      assertTrue(result);

      verify(workerRepo).findVacationCreditsByEmail("johnsmith@gmail.com");
      verify(requestRepo).getRequestedDaysById(5);
    }

    // 10. User doesn't have enough credits to make request
    @Test
    void testHasSufficientCreditsFalse() {
      Worker worker = new Worker();
      worker.setWorkerID(5);
      worker.setEmail("johnsmith@gmail.com");
      worker.setVacationCredits(2);

      when(workerRepo.findVacationCreditsByEmail("johnsmith@gmail.com")).thenReturn((long) 2);
      when(requestRepo.getRequestedDaysById(5)).thenReturn((long) 5);


      boolean result = service.hasSufficientCredits("johnsmith@gmail.com", 5);

      assertFalse(result);

      verify(workerRepo).findVacationCreditsByEmail("johnsmith@gmail.com");
      verify(requestRepo).getRequestedDaysById(5);
    }

}
