package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(summary= "Get a single RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="code") @RequestParam String code) {
        RecommendationRequest recreq = recommendationRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, code));

        return recreq;
    }

    @Operation(summary= "Create a new recommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postrecommendationRequest(
       
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name="professorEmail") @RequestParam String professorEmail,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name="localDateTime", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)", example="2023-12-01T13:15") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime,
        //@Parameter(name="setdateRequested") @RequestParam LocalDateTime local,
        //@Parameter(name="setdateNeeded") @RequestParam LocalDateTime setdateNeeded,
        @Parameter(name="done") @RequestParam boolean done)
        //@Parameter(name="longitude") @RequestParam double longitude)
        {

        RecommendationRequest recreq = new RecommendationRequest();
        recreq.setrequesterEmail(requesterEmail);
        recreq.setsetprofessorEmail(professorEmail);
        recreq.setexplanation(explanation);
        recreq.setdateRequested(localDateTime);
        recreq.setdateNeeded(localDateTime);
        recreq.setdone(done);
        //commons.setLongitude(longitude);

        RecommendationRequest savedrecreq = recommendationRequestRepository.save(recreq);

        return savedrecreq;
    }
    //commons
    @Operation(summary= "Delete a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleterecreq(
            @Parameter(name="code") @RequestParam String code) {
        RecommendationRequest recreq = recommendationRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, code));

        recommendationRequestRepository.delete(recreq);
        return genericMessage("Recommendationrequest with the id %s is deleted".formatted(code));
    }

    @Operation(summary= "Update a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updaterecreq(
            @Parameter(name="code") @RequestParam String code,
            @RequestBody @Valid RecommendationRequest newinfo) {

        RecommendationRequest recreq = recommendationRequestRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, code));


        recreq.setrequesterEmail(newinfo.getsetrequesterEmail());
        recreq.setprofessorEmail(newinfo.getsetprofessorEmail());
        recreq.setexplanation(newinfo.getexplanation());
        recreq.setdateRequested(newinfo.getdateRequested());
        recreq.setdateNeeded(newinfo.getdateNeeded());
        recreq.setdone(newinfo.getdone());

        recommendationRequestRepository.save(recreq);

        return recreq;
    }

}


