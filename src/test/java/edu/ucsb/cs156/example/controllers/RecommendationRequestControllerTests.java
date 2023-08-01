package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;

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

@WebMvcTest(RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecomendationRequestControllerTests extends ControllerTestCase{
    
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

   
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest/all"))
                        .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest/all"))
                        .andExpect(status().is(200)); 
    }

    @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_recomendationrequests() throws Exception {

                
                LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");
                LocalDateTime timetest2 = LocalDateTime.parse("2023-03-01T00:00:00");

                RecommendationRequest recreq1 = RecommendationRequest.builder()
                                .requesterEmail("emailtest1")
                                .professorEmail("emailtest2")
                                .explanation("explanationtest1")
                                .dateRequested(timetest1)
                                .dateNeeded(timetest2)
                                .done(false)
                                .build();
                RecommendationRequest recreq2 = RecommendationRequest.builder()
                                .requesterEmail("emailtest3")
                                .professorEmail("emailtest4")
                                .explanation("testexplanation2")
                                .dateRequested(timetest1)
                                .dateNeeded(timetest2)
                                .done(false)
                                .build();



                ArrayList<RecommendationRequest> reqs = new ArrayList<>();
                reqs.addAll(Arrays.asList(recreq1, recreq2));

                when(recommendationRequestRepository.findAll()).thenReturn(reqs);

                MvcResult results = mockMvc.perform(get("/api/recommendationrequest/all"))
                                .andExpect(status().isOk()).andReturn();

               

                verify(recommendationRequestRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(reqs);
                String resultsString = results.getresults().getContentAsString();
                assertEquals(expectedJson, resultsString);
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/recommendationrequest?id=1"))
                        .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

       
            LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");
            LocalDateTime timetest2 = LocalDateTime.parse("2023-03-01T00:00:00");

            RecommendationRequest recreq = RecommendationRequest.builder()
                                        .requesterEmail("emailtest1")
                                        .professorEmail("emailtest2")
                                        .explanation("explanationtest1")
                                        .dateRequested(timetest1)
                                        .dateNeeded(timetest2)
                                        .done(false)
                                        .build();

                                        
            when(recommendationRequestRepository.findById(eq(123L))).thenReturn(Optional.of(recreq));

           
            MvcResult results = mockMvc.perform(get("/api/recommendationrequest?id=123"))
                            .andExpect(status().isOk()).andReturn();

          
//code
            verify(recommendationRequestRepository, times(1)).findById(eq(123L));
            String expectedJson = mapper.writeValueAsString(recreq);
            String resultsString = results.getresults().getContentAsString();
            assertEquals(expectedJson, resultsString);
        }

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
        
        when(recommendationRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

        MvcResult results = mockMvc.perform(get("/api/recommendationrequest?id=123"))
                                .andExpect(status().isNotFound()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(eq(123L));
        Map<String, Object> json = resultsToJson(results);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("RecommendationRequest with the id 123 is not found", json.get("message"));
    }



    

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequest/post"))
                        .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequest/post"))
                        .andExpect(status().is(403)); 
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recomendationrequest() throws Exception {
        
        LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");
        LocalDateTime timetest2 = LocalDateTime.parse("2023-03-01T00:00:00");
        
        RecommendationRequest recreq = RecommendationRequest.builder()
            .requesterEmail("requester@ucsb.edu")
            .professorEmail("professor@ucsb.edu")
            .explanation("explanationtest1")
            .dateRequested(timetest1)
            .dateNeeded(timetest2)
            .done(false)
            .build();

        when(recommendationRequestRepository.save(eq(recreq))).thenReturn(recreq);

        MvcResult results = mockMvc.perform(post("/api/recommendationrequest/post?requestorEmail=requester@ucsb.edu&professorEmail=professor@ucsb.edu&explanation=explanationtest1&dateRequested=2023-01-01T00:00:00&dateNeeded=2023-03-01T00:00:00&done=false").with(csrf()))
                        .andExpect(status().isOk())
                        .andReturn();

        verify(recommendationRequestRepository, times(1)).save(recreq);
        String expectedJson = mapper.writeValueAsString(recreq);
        String resultsString = results.getresults().getContentAsString();

        assertEquals(expectedJson, resultsString);

    }

    

    @WithMockUser(roles = { "ADMIN", "USER" })//
        @Test
        public void admin_can_edit_an_existing_ucsbdate() throws Exception {

            LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");
            LocalDateTime timetest2 = LocalDateTime.parse("2023-03-01T00:00:00");

            RecommendationRequest recreq1 = RecommendationRequest.builder()
                            .requesterEmail("emailtest1")
                            .professorEmail("emailtest2")
                            .explanation("explanationtest1")
                            .dateRequested(timetest1)
                            .dateNeeded(timetest2)
                            .done(false)
                            .build();
            RecommendationRequest recreq2 = RecommendationRequest.builder()
                            .requesterEmail("emailtest3")
                            .explanation("explanationtest2")
                            .dateRequested(timetest2)
                            .dateNeeded(timetest1)
                            .done(true)
                            .build();

            String requestBody = mapper.writeValueAsString(recreq2);

            when(recommendationRequestRepository.findById(eq(57L))).thenReturn(Optional.of(recreq1));

            
            MvcResult results = mockMvc.perform(
                            put("/api/recommendationrequest?id=57")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

        
            verify(recommendationRequestRepository, times(1)).findById(57L);
            verify(recommendationRequestRepository, times(1)).save(recreq2); 
            String resultsString = results.getresults().getContentAsString();
            assertEquals(requestBody, resultsString);

        }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
        
        LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");

        RecommendationRequest recreq1 = RecommendationRequest.builder()
                        .requesterEmail("emailtest1")
                        .professorEmail("emailtest2")
                        .explanation("explanationtest1")
                        .dateRequested(timetest1)
                        .dateNeeded(timetest1)
                        .done(false)
                        .build();

    

        String requestBody = mapper.writeValueAsString(recreq1);

        when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

        MvcResult results = mockMvc.perform(
                        put("/api/recommendationrequest?id=67")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .characterEncoding("utf-8")
                                        .content(requestBody)
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

     
        verify(recommendationRequestRepository, times(1)).findById(67L);
        Map<String, Object> json = resultsToJson(results);
        assertEquals("RecommendationRequest with the id 67 is not found", json.get("message"));
    }


   

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_date() throws Exception {

        LocalDateTime timetest1 = LocalDateTime.parse("2023-01-01T00:00:00");
        LocalDateTime timetest2 = LocalDateTime.parse("2023-03-01T00:00:00");

        RecommendationRequest reqrec = RecommendationRequest.builder()
                        .requesterEmail("emailtest1")
                        .professorEmail("emailtest2")
                        .explanation("explanationtest1")
                        .dateRequested(timetest1)
                        .dateNeeded(timetest2)
                        .done(false)
                        .build();

        when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(reqrec));

            
        MvcResult results = mockMvc.perform(delete("/api/recommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

        verify(recommendationRequestRepository, times(1)).findById(15L);
        verify(recommendationRequestRepository, times(1)).delete(any());

        Map<String, Object> json = resultsToJson(results);
        assertEquals("RecommendationRequest with the id 15 is deleted", json.get("message"));

    }

    @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ucsbdate_and_gets_right_error_message() throws Exception {

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

            
            MvcResult results = mockMvc.perform(
                            delete("/api/recommendationrequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            
            verify(recommendationRequestRepository, times(1)).findById(15L);
            Map<String, Object> json = resultsToJson(results);
            assertEquals("RecommendationRequest with the id 15 is not found", json.get("message"));


//code//id

        }

}