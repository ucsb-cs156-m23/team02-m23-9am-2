package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

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

import java.time.LocalDateTime;

import javax.validation.Valid;

@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController {

    @Autowired
    MenuItemReviewRepository menuItemReviewRepository;

    @Operation(summary= "Menu Item Review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReview> allReviews() {
        Iterable<MenuItemReview> reviews = menuItemReviewRepository.findAll();
        return reviews;
    }

    @Operation(summary= "Get a single review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReview getById(
            @Parameter(name="code") @RequestParam String code) {
        MenuItemReview review = menuItemReviewRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, code));

        return review;
    }

    @Operation(summary= "Create a new review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReview postReview(
        @Parameter(name="id") @RequestParam long id,
        @Parameter(name="itemId") @RequestParam Long itemId,
        @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
        @Parameter(name="stars") @RequestParam int stars,
        @Parameter(name="localDateTime") @RequestParam LocalDateTime localDateTime,
        @Parameter(name="comments") @RequestParam String comments
        )
        {

        MenuItemReview review = new MenuItemReview();
        review.setId(id);
        review.setItemId(itemId);
        review.setReviewerEmail(reviewerEmail);
        review.setStars(stars);
        review.setLocalDateTime(localDateTime);
        review.setComments(comments);

        MenuItemReview savedReview = menuItemReviewRepository.save(review);

        return savedReview;
    }

    @Operation(summary= "Delete a MenuItemReview")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteReview(
            @Parameter(name="code") @RequestParam String code) {
        MenuItemReview review = menuItemReviewRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, code));

        menuItemReviewRepository.delete(review);
        return genericMessage("MenuItemReview with id %s deleted".formatted(code));
    }

    @Operation(summary= "Update a single review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReview updateReview(
            @Parameter(name="code") @RequestParam String code,
            @RequestBody @Valid MenuItemReview incoming) {

        MenuItemReview review = menuItemReviewRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, code));


        review.setId(incoming.getId());  
        review.setItemId(incoming.getItemId());
        review.setReviewerEmail(incoming.getReviewerEmail());
        review.setStars(incoming.getStars());
        review.setLocalDateTime(incoming.getLocalDateTime());
        //review.setComments(incoming.getComments());

        menuItemReviewRepository.save(review);

        return review;
    }
}
