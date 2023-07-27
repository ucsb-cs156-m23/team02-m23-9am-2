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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String code;
  private boolean hasSackMeal;
  private boolean hasTakeOutMeal;
  private boolean hasDiningCam;
  private Double latitude;
  private Double longitude;
  private long id;
  private String quarterYYYYQ;
  private String name;  
  private LocalDateTime localDateTime;
}
////////////////////////////////////////////






  
  






  
  
