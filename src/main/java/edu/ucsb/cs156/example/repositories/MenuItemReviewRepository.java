package edu.ucsb.cs156.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuItemReviewRepository extends CrudRepository<MenuItemReviewRepository, Long> {
  Iterable<MenuItemReviewRepository> findAllByItemIdIterable(long itemID);
}