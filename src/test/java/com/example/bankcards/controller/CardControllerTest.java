package com.example.bankcards.controller;

import com.example.bankcards.config.CorsConfig;
import com.example.bankcards.config.WebSecurityConfig;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.JwtAuthenticationEntryPoint;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.PaginationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import({WebSecurityConfig.class, CorsConfig.class})
@AutoConfigureMockMvc(addFilters = true)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private PaginationUtils paginationUtils;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Card testCard;
    private CardResponse cardResponse;
    private CardRequest cardRequest;
    private TransactionRequest transactionRequest;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("1234567890123456");
        testCard.setBalance(BigDecimal.valueOf(1000));
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setUser(testUser);


        cardResponse = new CardResponse(
                "1234 **** **** 3456",
                "Test User",
                "USD",
                LocalDate.now().plusYears(3),
                CardStatus.ACTIVE.name(),
                BigDecimal.valueOf(1000),
                true,
                false
        );

        cardRequest = new CardRequest();
        cardRequest.setCurrency("USD");

        transactionRequest = new TransactionRequest();
        transactionRequest.setFromCardId(1L);
        transactionRequest.setToCardId(2L);
        transactionRequest.setAmount(BigDecimal.valueOf(100));

        transactionResponse = new TransactionResponse(
                1L,
                BigDecimal.valueOf(100),
                "1234 **** **** 3456",
                "9876 **** **** 5432",
                "Transfer test",
                "SUCCESS",
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserCards_ShouldReturnCards() throws Exception {
        Page<CardResponse> page = new PageImpl<>(List.of(cardResponse));
        Mockito.when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        Mockito.when(cardService.getUserCardsPaginated(eq(testUser), anyString(), any(Pageable.class)))
                .thenReturn(page);
        Mockito.when(paginationUtils.createPageable(any(), any(), any(), any())).thenReturn(Pageable.unpaged());
        Mockito.when(paginationUtils.buildPaginationResponse(any())).thenReturn(Map.of("data", List.of(cardResponse)));

        mockMvc.perform(get("/api/cards/my-cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].maskedNumber").value("1234 **** **** 3456"));
    }

    @Test
    void getUserCards_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        SecurityContextHolder.clearContext(); // очистка контекста

        mockMvc.perform(get("/api/cards/my-cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserCardsActive_ShouldReturnActiveCards() throws Exception {
        Mockito.when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        Mockito.when(cardService.getCardsByUserAndStatus(eq(testUser), eq(CardStatus.ACTIVE)))
                .thenReturn(List.of(cardResponse));

        mockMvc.perform(get("/api/cards/my-cards/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maskedNumber").value("1234 **** **** 3456"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCard_ShouldCreateCard() throws Exception {
        Mockito.when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        Mockito.when(cardService.createCard(eq(testUser), eq("USD"))).thenReturn(cardResponse);

        mockMvc.perform(post("/api/cards/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedNumber").value("1234 **** **** 3456"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void blockCard_ShouldBlockCard() throws Exception {
        Mockito.when(cardService.cardExists(1L)).thenReturn(true);

        mockMvc.perform(post("/api/cards/block/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        Mockito.verify(cardService).blockCard(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void blockCard_WithNonExistingCard_ShouldReturnBadRequest() throws Exception {
        Mockito.when(cardService.cardExists(1L)).thenReturn(false);

        mockMvc.perform(post("/api/cards/block/1")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void transferCard_ShouldProcessTransfer() throws Exception {
        Mockito.when(transactionService.transferBetweenCards(any(TransactionRequest.class)))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/api/cards/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.fromCardNumber").value("1234 **** **** 3456"))
                .andExpect(jsonPath("$.toCardNumber").value("9876 **** **** 5432"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBalance_ShouldReturnBalance() throws Exception {
        Mockito.when(cardService.getCardByNumber("1234567890123456"))
                .thenReturn(Optional.ofNullable(testCard));

        mockMvc.perform(get("/api/cards/balance?cardNumber=1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_AsAdmin_ShouldReturnAllCards() throws Exception {
        Mockito.when(cardService.getAllCards()).thenReturn(List.of(cardResponse));

        mockMvc.perform(get("/api/cards/admin/all-cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maskedNumber").value("1234 **** **** 3456"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCards_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/cards/admin/all-cards"))
                .andExpect(status().isForbidden());
    }
}
