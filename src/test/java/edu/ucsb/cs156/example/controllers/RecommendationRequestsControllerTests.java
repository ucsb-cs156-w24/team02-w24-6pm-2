package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequests;
import edu.ucsb.cs156.example.repositories.RecommendationRequestsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestsController.class)
@Import(TestConfig.class)
public class RecommendationRequestsControllerTests extends ControllerTestCase {

        @MockBean
        RecommendationRequestsRepository recommendationRequestsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbdates/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/recommendationrequests/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/recommendationrequests/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_recommendationrequests() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-05-01T00:00:00");

                RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("BS/MS program")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                LocalDateTime ldt3 = LocalDateTime.parse("2022-05-20T00:00:00");
                LocalDateTime ldt4 = LocalDateTime.parse("2022-11-15T00:00:00");

                RecommendationRequests recommendationRequests2 = RecommendationRequests.builder()
                                .requesterEmail("ldelplaya@ucsb.edu")
                                .professorEmail("richert@ucsb.edu")
                                .Explanation("PhD CS Stanford")
                                .dateRequested(ldt3)
                                .dateNeeded(ldt4)
                                .done(true)
                                .build();

                LocalDateTime ldt5 = LocalDateTime.parse("2022-05-20T00:00:00");
                LocalDateTime ldt6 = LocalDateTime.parse("2022-11-15T00:00:00");

                RecommendationRequests recommendationRequests3 = RecommendationRequests.builder()
                                .requesterEmail("ldelplaya@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("PhD CS Stanford")
                                .dateRequested(ldt5)
                                .dateNeeded(ldt6)
                                .done(false)
                                .build();
                
                LocalDateTime ldt7 = LocalDateTime.parse("2022-05-20T00:00:00");
                LocalDateTime ldt8 = LocalDateTime.parse("2022-11-15T00:00:00");

                RecommendationRequests recommendationRequests4 = RecommendationRequests.builder()
                                .requesterEmail("alu@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("PhD CE Cal Tech")
                                .dateRequested(ldt7)
                                .dateNeeded(ldt8)
                                .done(false)
                                .build();


                ArrayList<RecommendationRequests> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(recommendationRequests1, recommendationRequests2, recommendationRequests3, recommendationRequests4));

                when(recommendationRequestsRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(recommendationRequestsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbdates/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/recommendationrequests/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/recommendationrequests/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-05-01T00:00:00");

                RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("BS/MS program")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(recommendationRequestsRepository.save(eq(recommendationRequests1))).thenReturn(recommendationRequests1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/recommendationrequests/post?requesterEmail=cgaucho@ucsb.edu&professorEmail=phtcon@ucsb.edu&Explanation=BS/MS program&dateRequested=2022-04-20T00:00:00&dateNeeded=2022-05-01T00:00:00&done=false")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestsRepository, times(1)).save(recommendationRequests1);
                String expectedJson = mapper.writeValueAsString(recommendationRequests1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_recommendationrequest1() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-05-01T00:00:00");

                RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("BS/MS program")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(true)
                                .build();

                when(recommendationRequestsRepository.save(eq(recommendationRequests1))).thenReturn(recommendationRequests1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/recommendationrequests/post?requesterEmail=cgaucho@ucsb.edu&professorEmail=phtcon@ucsb.edu&Explanation=BS/MS program&dateRequested=2022-04-20T00:00:00&dateNeeded=2022-05-01T00:00:00&done=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestsRepository, times(1)).save(recommendationRequests1);
                String expectedJson = mapper.writeValueAsString(recommendationRequests1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        // Tests for GET /api/ucsbdates?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-05-01T00:00:00");

                RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("BS/MS program")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(true)
                                .build();

                when(recommendationRequestsRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequests1));

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(recommendationRequestsRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(recommendationRequests1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

       @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_gets_not_found_when_id_does_not_exist() throws Exception {
        // arrange
        Long nonExistentId = 7L; // assuming ID 7 does not exist in the database for this test

        when(recommendationRequestsRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // act
        mockMvc.perform(get("/api/recommendationrequests?id=" + nonExistentId))
                .andExpect(status().isNotFound());
                
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test1_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(recommendationRequestsRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(recommendationRequestsRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequests with id 7 not found", json.get("message"));
        }

                // Tests for DELETE /api/ucsbdates?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_recommendationrequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-05-01T00:00:00");

                RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                                .requesterEmail("cgaucho@ucsb.edu")
                                .professorEmail("phtcon@ucsb.edu")
                                .Explanation("BS/MS program")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(recommendationRequestsRepository.findById(eq(15L))).thenReturn(Optional.of(recommendationRequests1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestsRepository, times(1)).findById(15L);
                verify(recommendationRequestsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequests with id 15 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_recommendationrequest_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(recommendationRequestsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestsRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequests with id 15 not found", json.get("message"));
        }

        
}
