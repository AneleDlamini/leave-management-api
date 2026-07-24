/* Defines the structure of the client (Worker) requesting vacation */

package com.assessment.time_off_request.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Component
@Entity // to create table
@Table(name = "REQUEST")
public class Request {

    public enum requestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }   
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment ID
    private int requestID; 
    private int workerID;
    private String startDate;
    private String endDate;

    @CreationTimestamp
    private Instant createdAt;

    private String assignedManager;
    private long requestedDays;

    // request status can either be APPROVED, REJECTED, PENDING
    @Enumerated(EnumType.STRING) 
    private requestStatus status = requestStatus.PENDING; // default status

    // constructors
     public Request() {
    }

    public Request(int requestID, int worker, Instant createdAt, String startDate, String endDate, long requestedDays, String assignedManager, String status) {
        this.requestID = requestID;
        this.workerID = worker;
        this.createdAt = createdAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestedDays = requestedDays;
        this.assignedManager = assignedManager;
    }


    // getters and setters
    public int getWorkerID() {
        return workerID;
    }



    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }



    public LocalDate getStartDate() {

        // Expected format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse the string to LocalDateTime
        LocalDate startDateFormatted = LocalDate.parse(startDate, formatter);

        // Convert LocalDateTime + timezone to Instant
        return startDateFormatted;
        
    
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;

    }

    public LocalDate getEndDate() {
         // Expected format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse the string to LocalDateTime
        LocalDate endDateFormatted = LocalDate.parse(endDate, formatter);

        // Convert LocalDateTime + timezone to Instant
        return endDateFormatted;
    }



    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }



    public Instant getCreatedAt() {
        return ZonedDateTime.now(ZoneId.of("Africa/Johannesburg")).toInstant();
    }



    public void setCreatedAt() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Africa/Johannesburg")).toInstant();
    }



    public String getAssignedManager() {
        return assignedManager;
    }



    public void setAssignedManager(String assignedManager) {
        this.assignedManager = assignedManager;
    }


    // Difference between requested days
    public long getRequestedDays() {
        return requestedDays;
    }



    public void setRequestedDays() {
        this.requestedDays = ChronoUnit.DAYS.between(getStartDate(), getEndDate());
    }



    public requestStatus getStatus() {
        return status;
    }



    public void setStatus(requestStatus status) {
        this.status = status;
    }

     public int getRequestID() {
         return requestID;
     }


     public void setRequestID(int requestID) {
         this.requestID = requestID;
     }


     @Override
     public String toString() {
        return "Request [requestID=" + getRequestID() + ", authorID=" + getWorkerID() + ", startDate=" + getStartDate()+ ", endDate="
                + getEndDate() + ", createdAt=" + getCreatedAt() +  ", requestedDays=" + getRequestedDays() + ", assignedManager=" + assignedManager + ", status=" + status + "]";
     }
    


    


}
