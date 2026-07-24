/* Loading dummy data into Worker and Request tables */
package com.assessment.time_off_request.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.time_off_request.model.Request;
import com.assessment.time_off_request.model.Worker;
import com.assessment.time_off_request.repo.RequestRepo;
import com.assessment.time_off_request.repo.WorkerRepo;


@Component
public class DatabaseSeeder implements CommandLineRunner{

    @Autowired
    private final WorkerRepo workerRepository;

    @Autowired
    private final RequestRepo requestRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    // constructor
    public DatabaseSeeder(WorkerRepo workerRepository, RequestRepo requestRepository, PasswordEncoder passwordEncoder) {
        this.workerRepository = workerRepository;
        this.requestRepository = requestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void run(String... args) throws Exception {
      //  System.out.println("Server started...");
        addWorkers();
        addRequests();
        
    }

    // Populate Workers database
    private void addWorkers() {
       // System.out.println("Seeding Workers..");

        if (workerRepository.count() > 0) return;

            Worker worker = new Worker();
            worker.setWorkerID(1);
            worker.setFirstName("Jane");
            worker.setLastName("Doe");
            worker.setEmail("janedoe@gmail.com");
            worker.setVacationCredits(15);
            worker.setRole("WORKER");
            worker.setPasswordHash(passwordEncoder.encode("janed@123"));
            workerRepository.save(worker);

            Worker worker2 = new Worker();
            worker2.setWorkerID(2);
            worker2.setFirstName("John");
            worker2.setLastName("Smith");
            worker2.setEmail("johnsmith@gmail.com");
            worker2.setVacationCredits(10);
            worker2.setRole("WORKER");
            worker2.setPasswordHash(passwordEncoder.encode("johns@123"));
            workerRepository.save(worker2);

            Worker worker3 = new Worker();
            worker3.setWorkerID(3);
            worker3.setFirstName("Bob");
            worker3.setLastName("Johnson");
            worker3.setEmail("bobjohnson@gmail.com");
            worker3.setVacationCredits(12);
            worker3.setRole("MANAGER");
            worker3.setPasswordHash(passwordEncoder.encode("bobj@123"));
            workerRepository.save(worker3);

            Worker worker4 = new Worker();
            worker4.setWorkerID(4);
            worker4.setFirstName("Alice");
            worker4.setLastName("Brown");
            worker4.setEmail("alicebrown@gmail.com");
            worker4.setVacationCredits(20);
            worker4.setRole("MANAGER");
            worker4.setPasswordHash(passwordEncoder.encode("aliceb@123"));
            workerRepository.save(worker4);

            Worker worker5 = new Worker();
            worker5.setWorkerID(5);
            worker5.setFirstName("Linda");
            worker5.setLastName("White");
            worker5.setEmail("lindawhite@gmail.com");
            worker5.setVacationCredits(8);
            worker5.setRole("WORKER");
            worker5.setPasswordHash(passwordEncoder.encode("lindaw@123"));
            workerRepository.save(worker5);


            //System.out.println("Workers database populated...");
    }

    // Populate Requests database
    private void addRequests() {

        if (requestRepository.count() > 0) return;

          //  System.out.println("Seeding first request...");

            Request request = new Request();
            request.setWorkerID(2);
            request.setAssignedManager("janedoe@gmail.com");
            request.getCreatedAt();
            request.setStartDate("23/05/2025");
            request.setEndDate("25/05/2025");
            request.setRequestedDays();
            requestRepository.save(request);

            Request request2 = new Request();
            request2.setWorkerID(1);
            request2.setAssignedManager("bobjohnson@gmail.com");
            request2.getCreatedAt();
            request2.setStartDate("25/08/2025");
            request2.setEndDate("29/08/2025");
            request2.setRequestedDays();
            requestRepository.save(request2);

            Request request3 = new Request();
            request3.setWorkerID(2);
            request3.setAssignedManager("janedoe@gmail.com");
            request3.getCreatedAt();
            request3.setStartDate("27/08/2025");
            request3.setEndDate("29/08/2025");
            request3.setRequestedDays();
            requestRepository.save(request3);
    }
}

