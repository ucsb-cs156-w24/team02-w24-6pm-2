package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

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

@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController{

    @Autowired
    MenuItemReviewRepository menuItemReviewRepository;

    @Operation(summary= "List all reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReview> allReviews() {
        Iterable<MenuItemReview> reviews = menuItemReviewRepository.findAll();
        return reviews;
    }

    @Operation(summary= "Create a new review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReview postMenuItemReview(
            @Parameter(name="itemID") @RequestParam long itemID,
            @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="localDateTime", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime,            @Parameter(name="comments") @RequestParam String comments)

    throws JsonProcessingException {

        //log.info("localDateTime={}", localDateTime);

        MenuItemReview menuItemReview = new MenuItemReview();
        menuItemReview.setItemId(itemID);
        menuItemReview.setReviewerEmail(reviewerEmail);
        menuItemReview.setStars(stars);
        menuItemReview.setDateReviewed(localDateTime);
        menuItemReview.setComments(comments);

        MenuItemReview savedReviews = menuItemReviewRepository.save(menuItemReview);

        return savedReviews;
    }

    @Operation(summary= "Delete a UCSBDate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDate(
            @Parameter(name="id") @RequestParam Long id) {
        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

        menuItemReviewRepository.delete(review);
        return genericMessage("UCSBDate with id %s deleted".formatted(id));
    }
}
