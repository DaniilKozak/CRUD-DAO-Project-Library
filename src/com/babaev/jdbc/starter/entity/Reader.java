package com.babaev.jdbc.starter.entity;

import java.time.LocalDate;

public class Reader {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate registrationDate;
    private boolean blackListed;

    public Reader() {
    }

    public Reader(Long id, String firstName, String lastName, String email, String phoneNumber,
                  LocalDate registrationDate, boolean blackListed) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.blackListed = blackListed;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public boolean isBlackListed() { return blackListed; }
    public void setBlackListed(boolean blackListed) { this.blackListed = blackListed; }

    @Override
    public String toString() {
        return "Reader{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", registrationDate=" + registrationDate +
               ", blackListed=" + blackListed +
               '}';
    }
}
