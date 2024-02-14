package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBArticles;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBArticlesRepository;

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

@Tag(name = "UCSBArticles")
@RequestMapping("/api/ucsbarticles")
@RestController
@Slf4j
public class ArticlesController extends ApiController {

    @Autowired
    UCSBArticlesRepository ucsbArticlesRepository;

    @Operation(summary= "List all ucsb articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBArticles> allUCSBArticles() {
        Iterable<UCSBArticles> articles = ucsbArticlesRepository.findAll();
        return articles;
    }

    @Operation(summary= "Create a new article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBArticles postUCSBArticles(
            @Parameter(name="title") @RequestParam String title,
            @Parameter(name="url") @RequestParam String url,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="email") @RequestParam String email,
            @Parameter(name="dateAdded", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateAdded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAdded)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", dateAdded);

        UCSBArticles ucsbArticle = new UCSBArticles();
        ucsbArticle.setTitle(title);
        ucsbArticle.setUrl(url);
        ucsbArticle.setExplanation(explanation);
        ucsbArticle.setEmail(email);
        ucsbArticle.setDateAdded(dateAdded);

        UCSBArticles savedUcsbArticle = ucsbArticlesRepository.save(ucsbArticle);

        return savedUcsbArticle;
    }

    // @Operation(summary= "Get a single article")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @GetMapping("")
    // public UCSBArticles getById(
    //         @Parameter(name="id") @RequestParam Long id) {
    //     UCSBArticles ucsbArticle = ucsbArticlesRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBArticles.class, id));

    //     return ucsbArticle;
    // }

    // @Operation(summary= "Delete a UCSBArticles")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("")
    // public Object deleteUCSBArticles(
    //         @Parameter(name="id") @RequestParam Long id) {
    //     UCSBArticles ucsbArticle = ucsbArticlesRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBArticles.class, id));

    //     ucsbArticlesRepository.delete(ucsbArticle);
    //     return genericMessage("UCSBArticle with id %s deleted".formatted(id));
    // }

    // @Operation(summary= "Update a single article")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("")
    // public UCSBArticles updateUCSBArticles(
    //         @Parameter(name="id") @RequestParam Long id,
    //         @RequestBody @Valid UCSBArticles incoming) {

    //     UCSBArticles ucsbArticle = ucsbArticlesRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBArticles.class, id));

    //     ucsbArticle.setQuarterYYYYQ(incoming.getQuarterYYYYQ());
    //     ucsbArticle.setName(incoming.getName());
    //     ucsbArticle.setLocalDateTime(incoming.getLocalDateTime());

    //     ucsbArticlesRepository.save(ucsbArticle);

    //     return ucsbArticle;
    // }
}
