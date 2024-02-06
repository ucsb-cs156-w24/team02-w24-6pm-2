package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.ReccomendationRequests;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReccommendationRequestsRespository extends CrudRepository<ReccomendationRequests, String> {
 
}