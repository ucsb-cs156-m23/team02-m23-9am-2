package edu.ucsb.cs156.example.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
////

import java.time.LocalDateTime;


import javax.persistence.GenerationType;

import javax.persistence.GeneratedValue;
////
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommendationrequest")
public class RecommendationRequest {
  @Id
  
  private String requesterEmai;
  private String professorEmail;
  private String explanation;
  private LocalDateTime setdateRequested;
  private LocalDateTime setdateNeeded;
  private boolean done;

}
////////////////////////////////////////////






  
  
