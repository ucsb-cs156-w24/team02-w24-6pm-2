package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

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

@Tag(name = "UCSBDiningCommonsMenuItems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

    @Operation(summary= "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItems> allCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItems> commons = ucsbDiningCommonsMenuItemsRepository.findAll();
        return commons;
    }

    @Operation(summary= "Create a new commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postCommonsMenuItems(
        @Parameter(name="id") @RequestParam Long id,
        @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="station") @RequestParam String station
        )
        {

        UCSBDiningCommonsMenuItems commonsMenuItems = new UCSBDiningCommonsMenuItems();
        commonsMenuItems.setId(id);
        commonsMenuItems.setDiningCommonsCode(diningCommonsCode);
        commonsMenuItems.setName(name);
        commonsMenuItems.setStation(station);

        UCSBDiningCommonsMenuItems savedCommonsMenuItems = ucsbDiningCommonsMenuItemsRepository.save(commonsMenuItems);

        return savedCommonsMenuItems;
    }

    @Operation(summary= "Get a single commons menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItems getById(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItems commonsMenuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        return commonsMenuItems;
    }

    @Operation(summary= "Update a single commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItems updateCommonsMenuItems(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBDiningCommonsMenuItems incoming) {

        UCSBDiningCommonsMenuItems commonsMenuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        commonsMenuItems.setId(incoming.getId());
        commonsMenuItems.setDiningCommonsCode(incoming.getDiningCommonsCode());
        commonsMenuItems.setName(incoming.getName());
        commonsMenuItems.setStation(incoming.getStation());

        ucsbDiningCommonsMenuItemsRepository.save(commonsMenuItems);

        return commonsMenuItems;
    }

    
}
