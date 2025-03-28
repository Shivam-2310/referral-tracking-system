package com.shivam.referral_tracking_system.service;

import com.shivam.referral_tracking_system.dto.ProfileCompletionRequest;
import com.shivam.referral_tracking_system.dto.ReferralResponse;
import com.shivam.referral_tracking_system.dto.SignupRequest;
import com.shivam.referral_tracking_system.entity.Referral;
import com.shivam.referral_tracking_system.entity.ReferralStatus;
import com.shivam.referral_tracking_system.entity.User;
import com.shivam.referral_tracking_system.repository.ReferralRepository;
import com.shivam.referral_tracking_system.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReferralRepository referralRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignupWithoutReferral() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("TEST USER");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setReferralCode(null);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("TEST USER");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password");
        savedUser.setReferralCode("ABCD1234");
        savedUser.setProfileCompleted(false);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.signup(signupRequest);
        assertNotNull(result);
        assertEquals("TEST USER", result.getName());
        assertFalse(result.isProfileCompleted());
        assertNotNull(result.getReferralCode());
        verify(userRepository, times(1)).save(any(User.class));
        verify(referralRepository, never()).save(any(Referral.class));
    }

    @Test
    public void testSignupWithReferral() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("REFERRED USER");
        signupRequest.setEmail("referred@example.com");
        signupRequest.setPassword("password");
        signupRequest.setReferralCode("REF12345");

        User referrer = new User();
        referrer.setId(2L);
        referrer.setName("REFERRER USER");
        referrer.setReferralCode("REF12345");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("REFERRED USER");
        savedUser.setEmail("referred@example.com");
        savedUser.setPassword("password");
        savedUser.setReferralCode("GENCODE");
        savedUser.setProfileCompleted(false);

        when(userRepository.findByReferralCode("REF12345")).thenReturn(Optional.of(referrer));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.signup(signupRequest);
        assertNotNull(result);
        verify(userRepository, atLeast(2)).save(any(User.class));
        verify(referralRepository, times(1)).save(any(Referral.class));
    }

    @Test
    public void testCompleteProfileWithReferral() {
        User user = new User();
        user.setId(1L);
        user.setName("USER");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setProfileCompleted(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Referral referral = new Referral();
        referral.setId(10L);
        referral.setReferrer(new User());
        referral.setReferred(user);
        referral.setStatus(ReferralStatus.PENDING);
        when(referralRepository.findByReferred(user)).thenReturn(Optional.of(referral));
        when(referralRepository.save(any(Referral.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileCompletionRequest profileRequest = new ProfileCompletionRequest();
        profileRequest.setUserId(1L);
        profileRequest.setMobileNumber("1234567890");
        profileRequest.setGender("MALE");
        profileRequest.setAddress("123 STREET");

        User updatedUser = userService.completeProfile(profileRequest);
        assertTrue(updatedUser.isProfileCompleted());
        assertEquals("1234567890", updatedUser.getMobileNumber());
        verify(userRepository, times(1)).save(any(User.class));
        verify(referralRepository, times(1)).save(any(Referral.class));
    }

    @Test
    public void testGetReferralsDto() {
        User referrer = new User();
        referrer.setId(1L);
        referrer.setName("REFERRER");

        User referred1 = new User();
        referred1.setId(2L);
        referred1.setName("REFERRED ONE");

        User referred2 = new User();
        referred2.setId(3L);
        referred2.setName("REFERRED TWO");

        Referral referral1 = new Referral();
        referral1.setId(100L);
        referral1.setReferrer(referrer);
        referral1.setReferred(referred1);
        referral1.setStatus(ReferralStatus.SUCCESSFUL);

        Referral referral2 = new Referral();
        referral2.setId(101L);
        referral2.setReferrer(referrer);
        referral2.setReferred(referred2);
        referral2.setStatus(ReferralStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(referrer));
        when(referralRepository.findByReferrer(referrer)).thenReturn(Arrays.asList(referral1, referral2));

        List<ReferralResponse> responses = userService.getReferralsDto(1L);
        assertEquals(2, responses.size());
        ReferralResponse first = responses.get(0);
        assertEquals(100L, first.getReferralId());
        assertEquals("REFERRED ONE", first.getReferredName());
    }

    @Test
    public void testGenerateReferralReport() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("USER ONE");
        user1.setEmail("user1@example.com");
        user1.setReferralCode("CODE1");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("USER TWO");
        user2.setEmail("user2@example.com");
        user2.setReferralCode("CODE2");

        Referral referral = new Referral();
        referral.setId(10L);
        referral.setReferrer(user1);
        referral.setReferred(user2);
        referral.setStatus(ReferralStatus.SUCCESSFUL);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(referralRepository.findByReferrer(user1)).thenReturn(Arrays.asList(referral));
        when(referralRepository.findByReferrer(user2)).thenReturn(Collections.emptyList());

        ByteArrayInputStream reportStream = userService.generateReferralReport();
        assertNotNull(reportStream);

        String csvContent = new String(reportStream.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("User ID"));
        assertTrue(csvContent.contains("USER ONE"));
        assertTrue(csvContent.contains("USER TWO"));
    }
}