package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository; 


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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbdiningcommonsmenuitems/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbDiningCommonsMenuItemsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=15"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(eq(15L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBDiningCommonsMenuItems with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitems() throws Exception {

                // arrange

                UCSBDiningCommonsMenuItems ortega = UCSBDiningCommonsMenuItems.builder()
                                .id(15L)
                                .diningCommonsCode("ortega")
                                .name("Baked Pesto Pasta with Chicken")
                                .station("Entree Specials")
                                .build();

                UCSBDiningCommonsMenuItems portola = UCSBDiningCommonsMenuItems.builder()
                                .id(15L)
                                .diningCommonsCode("portola")
                                .name("Cream of Broccoli Soup (v)")
                                .station("Greens & Grains")
                                .build();

                ArrayList<UCSBDiningCommonsMenuItems> expectedCommonsMenuItems = new ArrayList<>();
                expectedCommonsMenuItems.addAll(Arrays.asList(ortega, portola));

                when(ucsbDiningCommonsMenuItemsRepository.findAll()).thenReturn(expectedCommonsMenuItems);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedCommonsMenuItems);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbdiningcommonsmenuitems...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_commonsmenuitems() throws Exception {
                // arrange

                UCSBDiningCommonsMenuItems ortega = UCSBDiningCommonsMenuItems.builder()
                                .id(67L)
                                .diningCommonsCode("ortega")
                                .name("BakedPestoPastawithChicken")
                                .station("EntreeSpecials")
                                .build();

                when(ucsbDiningCommonsMenuItemsRepository.save(eq(ortega))).thenReturn(ortega);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbdiningcommonsmenuitems/post?id=67&diningCommonsCode=ortega&name=BakedPestoPastawithChicken&station=EntreeSpecials")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).save(ortega);
                String expectedJson = mapper.writeValueAsString(ortega);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/ucsbdates?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=67"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBDiningCommonsMenuItems commonsMenuItems = UCSBDiningCommonsMenuItems.builder()
                                .id(67L)
                                .diningCommonsCode("ortega")
                                .name("BakedPestoPastawithChicken")
                                .station("EntreeSpecials")
                                .build();

                when(ucsbDiningCommonsMenuItemsRepository.findById(eq(67L))).thenReturn(Optional.of(commonsMenuItems));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=67"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(eq(67L));
                String expectedJson = mapper.writeValueAsString(commonsMenuItems);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);

                // // arrange
                // LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

                // UCSBDate ucsbDate = UCSBDate.builder()
                //                 .name("firstDayOfClasses")
                //                 .quarterYYYYQ("20222")
                //                 .localDateTime(ldt)
                //                 .build();

                // when(ucsbDateRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDate));

                // // act
                // MvcResult response = mockMvc.perform(get("/api/ucsbdates?id=7"))
                //                 .andExpect(status().isOk()).andReturn();

                // // assert

                // verify(ucsbDateRepository, times(1)).findById(eq(7L));
                // String expectedJson = mapper.writeValueAsString(ucsbDate);
                // String responseString = response.getResponse().getContentAsString();
                // assertEquals(expectedJson, responseString);
        }

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        //         // arrange

        //         when(ucsbDiningCommonsMenuItemsRepository.findById(eq(67L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=67"))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert

        //         verify(ucsbDiningCommonsMenuItemsRepository, times(1)).findById(eq(67L));
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("EntityNotFoundException", json.get("type"));
        //         assertEquals("UCSBDiningCommonsMenuItems with id 67 not found", json.get("message"));
        // }
}