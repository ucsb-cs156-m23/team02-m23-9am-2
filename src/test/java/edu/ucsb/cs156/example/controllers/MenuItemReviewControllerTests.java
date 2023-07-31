package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import java.time.LocalDateTime;
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

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase {

        @MockBean
        MenuItemReviewRepository menuItemReviewRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/menuitemreview/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/menuitemreview?id=12345"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/menuitemreview/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2023-07-27T22:17:03.403");
                MenuItemReview mir = MenuItemReview.builder()
                                .itemId(456L)
                                .reviewerEmail("brett@ucsb.edu")
                                .stars(4)
                                .localDateTime(ldt)
                                .comments("I love bread")
                                .build();

                when(menuItemReviewRepository.findById(eq(23L))).thenReturn(Optional.of(mir));

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=23"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findById(eq(23L));
                String expectedJson = mapper.writeValueAsString(mir);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(menuItemReviewRepository.findById(eq(987654321L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview?id=987654321"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findById(eq(987654321L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("MenuItemReview with id 987654321 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_menuitemreview() throws Exception {

                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2023-07-27T22:17:03.403");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-07-27T22:17:03.403");

                MenuItemReview mir1 = MenuItemReview.builder()
                                .itemId(526L)
                                .reviewerEmail("naggarond@ucsb.edu")
                                .stars(3)
                                .localDateTime(ldt)
                                .comments("I love American values")
                                .build();

                MenuItemReview mir2 = MenuItemReview.builder()
                                .itemId(527L)
                                .reviewerEmail("norsca@ucsb.edu")
                                .stars(5)
                                .localDateTime(ldt2)
                                .comments("I really love bread")
                                .build();

                ArrayList<MenuItemReview> expectedReview = new ArrayList<>();
                expectedReview.addAll(Arrays.asList(mir1, mir2));

                when(menuItemReviewRepository.findAll()).thenReturn(expectedReview);

                // act
                MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(menuItemReviewRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedReview);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_commons() throws Exception {
                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2023-07-27T22:17:03.403");

                MenuItemReview review = MenuItemReview.builder()
                                .itemId(56L)
                                .reviewerEmail("brettania@ucsb.edu")
                                .stars(3)
                                .localDateTime(ldt)
                                .comments("I am mid about bread")
                                .build();

                when(menuItemReviewRepository.save(eq(review))).thenReturn(review);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/menuitemreview/post")
                                .param("itemId","56")
                                .param("reviewerEmail","brettania@ucsb.edu")
                                .param("stars","3")
                                .param("localDateTime","2023-07-27T22:17:03.403")
                                .param("comments","I am mid about bread")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).save(review);
                String expectedJson = mapper.writeValueAsString(review);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2023-07-27T22:17:03.403");

                MenuItemReview review3 = MenuItemReview.builder()
                                .itemId(510L)
                                .reviewerEmail("ultima@ucsb.edu")
                                .stars(5)
                                .localDateTime(ldt)
                                .comments("Ultimate bread lover")
                                .build();

                when(menuItemReviewRepository.findById(eq(51L))).thenReturn(Optional.of(review3));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=51")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(51L);
                verify(menuItemReviewRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 51 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_commons_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(menuItemReviewRepository.findById(eq(414141L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/menuitemreview?id=414141")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(414141L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 414141 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_commons() throws Exception {
                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2013-07-27T22:17:03.403");
                LocalDateTime ldt2 = LocalDateTime.parse("2023-07-27T22:17:03.403");

                MenuItemReview review4 = MenuItemReview.builder()
                                .itemId(44L)
                                .reviewerEmail("fourfourfourfour@ucsb.edu")
                                .stars(4)
                                .localDateTime(ldt)
                                .comments("eats 4 breads")
                                .build();

                MenuItemReview review5 = MenuItemReview.builder()
                                .itemId(55L)
                                .reviewerEmail("fives@ucsb.edu")
                                .stars(5)
                                .localDateTime(ldt2)
                                .comments("fives has five letters")
                                .build();

                String requestBody = mapper.writeValueAsString(review5);

                when(menuItemReviewRepository.findById(eq(4L))).thenReturn(Optional.of(review4));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=4")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(4L);
                verify(menuItemReviewRepository, times(1)).save(review5); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_commons_that_does_not_exist() throws Exception {
                // arrange
                LocalDateTime ldt = LocalDateTime.parse("2013-07-27T22:17:03.403");

                MenuItemReview editedReview = MenuItemReview.builder()
                                .itemId(55L)
                                .reviewerEmail("fives@ucsb.edu")
                                .stars(5)
                                .localDateTime(ldt)
                                .comments("fives has five letters")
                                .build();

                String requestBody = mapper.writeValueAsString(editedReview);

                when(menuItemReviewRepository.findById(eq(4L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/menuitemreview?id=4")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(menuItemReviewRepository, times(1)).findById(4L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("MenuItemReview with id 4 not found", json.get("message"));

        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_set_id_from_user() throws Exception {
                mockMvc.perform(post("/api/menuitemreview/post"))
                                .andExpect(status().is(403)); // only admins can post
        }
}
