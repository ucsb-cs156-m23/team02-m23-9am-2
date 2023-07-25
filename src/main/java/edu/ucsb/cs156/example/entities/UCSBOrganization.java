package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
/*The `UCSBOrganization` table will use the `orgCode` field (a String) as its `@Id` field, and will have these columns:
 * String orgCode * String orgTranslationShort * String orgTranslation * boolean inactive Here are some sample values: 
 * | orgCode | orgTranslationShort | orgTranslation | inactive | |----|-------------------|------|---------| | ZPR | ZETA PHI RHO 
 * | ZETA PHI RHO | false | | SKY | SKYDIVING CLUB | SKYDIVING CLUB AT UCSB | false | | OSLI 
 * | STUDENT LIFE | OFFICE OF STUDENT LIFE | false | | KRC | KOREAN RADIO CL | KOREAN RADIO CLUB | false | */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsborganizations")
public class UCSBOrganization {
  @Id
  private String orgCode;
  private String orgTranslationShort;  
  private String orgTranslation;
  private boolean inactive;
}