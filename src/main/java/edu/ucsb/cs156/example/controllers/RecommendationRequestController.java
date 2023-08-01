package edu.ucsb.cs156.example.controllers;
import edu.ucsb.cs156.example.controllers.ApiController;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/RecommendationRequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    @Operation(summary= "List all RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequest() {
        Iterable<RecommendationRequest> recreq = recommendationRequestRepository.findAll();
        return recreq;
    }
//id
    @Operation(summary= "Get a single RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recreq = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recreq;
        //recommendationRequestRepository.getById(recreq);
        //return genericMessage("Recommendationrequest with the id %s is not found".formatted(id));
    }
//id
    @Operation(summary= "Create a new recommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postrecommendationRequest(
       
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name="dateRequested", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)", example="2023-12-01T13:15") @RequestParam("dateRequested") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
        @Parameter(name="dateNeeded", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)", example="2023-12-01T13:15") @RequestParam("dateNeeded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
        //@Parameter(name="setdateRequested") @RequestParam LocalDateTime local,
        //@Parameter(name="setdateNeeded") @RequestParam LocalDateTime setdateNeeded,
        @Parameter(name="done") @RequestParam boolean done)
        //@Parameter(name="longitude") @RequestParam double longitude)
        {

        RecommendationRequest recreq = new RecommendationRequest();
        recreq.setRequesterEmail(requesterEmail);
        recreq.setProfessorEmail(professorEmail);
        recreq.setExplanation(explanation);
        recreq.setDateRequested(dateRequested);
        recreq.setDateNeeded(dateNeeded);
        recreq.setDone(done);
        //commons.setLongitude(longitude);

        RecommendationRequest savedrecreq = recommendationRequestRepository.save(recreq);

        return savedrecreq;
    }
    //commons
    @Operation(summary= "Delete a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleterecreq(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recreq = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recreq);
        return genericMessage("Recommendationrequest with the id %s is deleted".formatted(id));
    }

    @Operation(summary= "Update a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updaterecreq(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest newinfo) {

        RecommendationRequest recreq = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));


        recreq.setRequesterEmail(newinfo.getRequesterEmail());
        recreq.setProfessorEmail(newinfo.getProfessorEmail());
        recreq.setExplanation(newinfo.getExplanation());
        recreq.setDateRequested(newinfo.getDateRequested());
        recreq.setDateNeeded(newinfo.getDateNeeded());
        recreq.setDone(newinfo.getDone());

        recommendationRequestRepository.save(recreq);

        return recreq;
    }

}