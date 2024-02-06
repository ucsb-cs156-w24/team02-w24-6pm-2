package edu.ucsb.cs156.example.controllers;

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
@RequestMapping("/api/recommendationrequests")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRequests() {
        return recommendationRequestRepository.findAll();
    }

    @Operation(summary= "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRequest(
        @Parameter(description="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(description="professorEmail") @RequestParam String professorEmail,
        @Parameter(description="explanation") @RequestParam String explanation,
        @Parameter(description="dateRequested") @RequestParam LocalDateTime dateRequested,
        @Parameter(description="dateNeeded") @RequestParam LocalDateTime dateNeeded,
        @Parameter(description="done") @RequestParam boolean done
        )
        {

        RecommendationRequest request = new RecommendationRequest();
        request.setRequesterEmail(requesterEmail);
        request.setProfessorEmail(professorEmail);
        request.setExplanation(explanation);
        request.setDateRequested(dateRequested);
        request.setDateNeeded(dateNeeded);
        request.setDone(done);

        return recommendationRequestRepository.save(request);
    }

    @Operation(summary= "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getRequestById(
            @Parameter(description="id of the recommendation request") @RequestParam Long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));
    }

    @Operation(summary= "Delete a recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRequest(
            @Parameter(description="id of the recommendation request") @RequestParam Long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(request);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRequest(
            @Parameter(description="id of the recommendation request") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        request.setRequesterEmail(incoming.getRequesterEmail());
        request.setProfessorEmail(incoming.getProfessorEmail());
        request.setExplanation(incoming.getExplanation());
        request.setDateRequested(incoming.getDateRequested());
        request.setDateNeeded(incoming.getDateNeeded());
        request.setDone(incoming.isDone());

        return recommendationRequestRepository.save(request);
    }
}