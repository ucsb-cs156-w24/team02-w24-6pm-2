package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBArticles;
import edu.ucsb.cs156.example.repositories.UCSBArticlesRepository;

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

@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {

        @MockBean
        UCSBArticlesRepository ucsbArticlesRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbarticles/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbarticles/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbarticles/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbarticles() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBArticles ucsbArticle1 = UCSBArticles.builder()
                                .title("a")
                                .url("b")
                                .explanation("c")
                                .email("d")
                                .dateAdded(ldt1)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBArticles ucsbArticle2 = UCSBArticles.builder()
                                .title("e")
                                .url("f")
                                .explanation("g")
                                .email("h")
                                .dateAdded(ldt2)
                                .build();

                ArrayList<UCSBArticles> expectedArticles = new ArrayList<>();
                expectedArticles.addAll(Arrays.asList(ucsbArticle1, ucsbArticle2));

                when(ucsbArticlesRepository.findAll()).thenReturn(expectedArticles);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbarticles/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbArticlesRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedArticles);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbarticles/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbarticles/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbarticles/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbarticles() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                UCSBArticles ucsbArticles1 = UCSBArticles.builder()
                                .title("a")
                                .url("b")
                                .explanation("c")
                                .email("d")
                                .dateAdded(ldt1)
                                .build();

                when(ucsbArticlesRepository.save(eq(ucsbArticles1))).thenReturn(ucsbArticles1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbarticles/post?title=a&url=b&explanation=c&email=d&dateAdded=2022-01-03T00:00:00")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbArticlesRepository, times(1)).save(ucsbArticles1);
                String expectedJson = mapper.writeValueAsString(ucsbArticles1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // // Tests for GET /api/ucsbarticles?id=...

        // @Test
        // public void logged_out_users_cannot_get_by_id() throws Exception {
        //         mockMvc.perform(get("/api/ucsbarticles?id=7"))
        //                         .andExpect(status().is(403)); // logged out users can't get by id
        // }

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        //         // arrange
        //         LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBArticles ucsbArticles = UCSBArticles.builder()
        //                         .title("i")
        //                         .url("j")
        //                         .explanation("k")
        //                         .email("l")
        //                         .dateAdded(ldt)
        //                         .build();

        //         when(ucsbArticlesRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbArticles));

        //         // act
        //         MvcResult response = mockMvc.perform(get("/api/ucsbarticles?id=7"))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert

        //         verify(ucsbArticlesRepository, times(1)).findById(eq(7L));
        //         String expectedJson = mapper.writeValueAsString(ucsbArticles);
        //         String responseString = response.getResponse().getContentAsString();
        //         assertEquals(expectedJson, responseString);
        // }

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        //         // arrange

        //         when(ucsbArticlesRepository.findById(eq(7L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(get("/api/ucsbarticles?id=7"))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert

        //         verify(ucsbArticlesRepository, times(1)).findById(eq(7L));
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("EntityNotFoundException", json.get("type"));
        //         assertEquals("UCSBArticles with id 7 not found", json.get("message"));
        // }


        // // Tests for DELETE /api/ucsbarticles?id=... 

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_can_delete_a_article() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBArticles ucsbArticle1 = UCSBArticles.builder()
        //                         .title("a")
        //                         .url("b")
        //                         .explanation("c")
        //                         .email("d")
        //                         .dateAdded(ldt1)
        //                         .build();

        //         when(ucsbArticlesRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbArticle1));

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         delete("/api/ucsbarticles?id=15")
        //                                         .with(csrf()))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert
        //         verify(ucsbArticlesRepository, times(1)).findById(15L);
        //         verify(ucsbArticlesRepository, times(1)).delete(any());

        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBArticles with id 15 deleted", json.get("message"));
        // }
        
        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_tries_to_delete_non_existant_ucsbarticle_and_gets_right_error_message()
        //                 throws Exception {
        //         // arrange

        //         when(ucsbArticlesRepository.findById(eq(15L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         delete("/api/ucsbarticles?id=15")
        //                                         .with(csrf()))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert
        //         verify(ucsbArticlesRepository, times(1)).findById(15L);
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBArticles with id 15 not found", json.get("message"));
        // }

        // // Tests for PUT /api/ucsbarticles?id=... 

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_can_edit_an_existing_ucsbarticle() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
        //         LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

        //         UCSBArticles ucsbArticleOrig = UCSBArticles.builder()
        //                         .title("a")
        //                         .url("b")
        //                         .explanation("c")
        //                         .email("d")
        //                         .dateAdded(ldt1)
        //                         .build();

        //         UCSBArticles ucsbArticleEdited = UCSBArticles.builder()
        //                         .title("aa")
        //                         .url("bb")
        //                         .explanation("cc")
        //                         .email("dd")
        //                         .dateAdded(ldt2)
        //                         .build();

        //         String requestBody = mapper.writeValueAsString(ucsbArticleEdited);

        //         when(ucsbArticlesRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbArticleOrig));

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         put("/api/ucsbarticles?id=67")
        //                                         .contentType(MediaType.APPLICATION_JSON)
        //                                         .characterEncoding("utf-8")
        //                                         .content(requestBody)
        //                                         .with(csrf()))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert
        //         verify(ucsbArticlesRepository, times(1)).findById(67L);
        //         verify(ucsbArticlesRepository, times(1)).save(ucsbArticleEdited); // should be saved with correct user
        //         String responseString = response.getResponse().getContentAsString();
        //         assertEquals(requestBody, responseString);
        // }

        
        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_cannot_edit_ucsbarticle_that_does_not_exist() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBArticles ucsbEditedArticle = UCSBArticles.builder()
        //                         .title("a")
        //                         .url("b")
        //                         .explanation("c")
        //                         .email("d")
        //                         .dateAdded(ldt1)
        //                         .build();

        //         String requestBody = mapper.writeValueAsString(ucsbEditedArticle);

        //         when(ucsbArticlesRepository.findById(eq(67L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         put("/api/ucsbarticles?id=67")
        //                                         .contentType(MediaType.APPLICATION_JSON)
        //                                         .characterEncoding("utf-8")
        //                                         .content(requestBody)
        //                                         .with(csrf()))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert
        //         verify(ucsbArticlesRepository, times(1)).findById(67L);
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBArticles with id 67 not found", json.get("message"));
        // }
}
