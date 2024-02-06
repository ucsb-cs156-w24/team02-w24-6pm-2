package edu.ucsb.cs156.example.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommendationrequest")
public class RecommendationRequests  {
  @Id
  private String requesterEmail;
  private String professorEmail;
  private String Explanation;
  private LocalDateTime dateRequested;
  private LocalDateTime dateNeeded;
  boolean done;

}