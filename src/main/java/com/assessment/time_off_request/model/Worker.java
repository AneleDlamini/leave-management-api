/* Worker class to construct information that defines a worker */

package com.assessment.time_off_request.model;

import java.util.Collection;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Component
@Entity // to create table
@Table(name = "WORKER")
public class Worker implements UserDetails{

    // local variables
    @Id
    private int workerID;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private long vacationCredits;

    // Generate constructors
    public Worker(){

    }

    public Worker(int workerID, String firstName, String lastName, String email, String role) {
        this.workerID = workerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.vacationCredits = 30;
    }

    

    public Worker(int workerID) {
        this.workerID = workerID;
    }

    public Worker(String email) {
        this.email = email;
    }

    // Generate getters and setters
    public int getWorkerID() {
        return workerID;
    } 
    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public long getVacationCredits() {
        return vacationCredits;
    }
    public void setVacationCredits(long vacationCredits) {
        this.vacationCredits = vacationCredits;
    }

    @Override
    public String toString() {
        return "Worker [workerID=" + workerID + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                + email + ", role=" + role + ", vacationCredits=" + vacationCredits + "]";
    }

    // Authentication functions
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
    }

    @Override
    public String getPassword() {
        return password;
        
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
    }

    // Setting hashed password
    public void setPasswordHash(String encode) {
        this.password = encode;
    }

    


}
