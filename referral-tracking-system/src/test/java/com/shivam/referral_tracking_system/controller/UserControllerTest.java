package com.shivam.referral_tracking_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.referral_tracking_system.dto.ProfileCompletionRequest;
import com.shivam.referral_tracking_system.dto.ReferralResponse;
import com.shivam.referral_tracking_system.dto.SignupRequest;
import com.shivam.referral_tracking_system.entity.ReferralStatus;
import com.shivam.referral_tracking_system.entity.User;
import com.shivam.referral_tracking_system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void testSignupEndpoint() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setName("JOHN DOE");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setReferralCode(null);

        User user = new User();
        user.setId(1L);
        user.setName("JOHN DOE");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setReferralCode("ABCD1234");
        user.setProfileCompleted(false);

        when(userService.signup(any(SignupRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("JOHN DOE")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.referralCode", is("ABCD1234")))
                .andExpect(jsonPath("$.profileCompleted", is(false)));
    }

    @Test
    public void testCompleteProfileEndpoint() throws Exception {
        ProfileCompletionRequest request = new ProfileCompletionRequest();
        request.setMobileNumber("1234567890");
        request.setGender("MALE");
        request.setAddress("123 MAIN ST, CITY");

        User user = new User();
        user.setId(1L);
        user.setName("JOHN DOE");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setMobileNumber("1234567890");
        user.setGender("MALE");
        user.setAddress("123 MAIN ST, CITY");
        user.setProfileCompleted(true);

        when(userService.completeProfile(any(ProfileCompletionRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/1/complete-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mobileNumber", is("1234567890")))
                .andExpect(jsonPath("$.gender", is("MALE")))
                .andExpect(jsonPath("$.address", is("123 MAIN ST, CITY")))
                .andExpect(jsonPath("$.profileCompleted", is(true)));
    }

    @Test
    public void testGetReferralsEndpoint() throws Exception {
        ReferralResponse response1 = new ReferralResponse(100L, 1L, "JOHN DOE", 2L, "ALICE SMITH", ReferralStatus.SUCCESSFUL);
        ReferralResponse response2 = new ReferralResponse(101L, 1L, "JOHN DOE", 3L, "BOB JOHNSON", ReferralStatus.PENDING);
        List<ReferralResponse> referralResponses = Arrays.asList(response1, response2);

        when(userService.getReferralsDto(eq(1L))).thenReturn(referralResponses);

        mockMvc.perform(get("/api/referrals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].referralId", is(100)))
                .andExpect(jsonPath("$[0].referrerName", is("JOHN DOE")))
                .andExpect(jsonPath("$[0].referredName", is("ALICE SMITH")))
                .andExpect(jsonPath("$[1].referralId", is(101)))
                .andExpect(jsonPath("$[1].referredName", is("BOB JOHNSON")));
    }

    @Test
    public void testReferralReportEndpoint() throws Exception {
        String csvData = "User ID,Name,Email,Referral Code,Referred Users,Referred Users Count\n"
                + "1,JOHN DOE,john@example.com,ABCD1234,Alice,Bob,2\n";
        ByteArrayInputStream stream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        when(userService.generateReferralReport()).thenReturn(stream);

        mockMvc.perform(get("/api/referral-report"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=referral_report.csv")))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(csvData));
    }
}
