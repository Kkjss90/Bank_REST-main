package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Card CRUD")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CardController {
    private final CardService cardService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final PaginationUtils paginationUtils;


    @GetMapping("/my-cards")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить мои карты", description = "Возвращает список карт текущего пользователя")
    public ResponseEntity<Map<String, Object>> getUserCards(Authentication authentication,
                                                            @RequestParam(required = false) Integer page,
                                                            @RequestParam(required = false) Integer size,
                                                            @RequestParam(required = false) String sortBy,
                                                            @RequestParam(required = false) String direction,
                                                            @RequestParam(required = false) String search) {
        Pageable pageable = paginationUtils.createPageable(page, size, sortBy, direction);
        Page<CardResponse> cardsPage = cardService.getUserCardsPaginated(
                userService.getUserByUsername(authentication.getName()), search, pageable);

        return ResponseEntity.ok(paginationUtils.buildPaginationResponse(cardsPage));
    }

    @GetMapping("/my-cards/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить мои активные карты", description = "Возвращает список активных карт текущего пользователя")
    public ResponseEntity<List<CardResponse>> getUserCardsActive(Authentication authentication) {
        List<CardResponse> cards = cardService.getCardsByUserAndStatus(userService.getUserByUsername(authentication.getName()), CardStatus.ACTIVE);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Создать карту", description = "Создает новую карту для текущего пользователя")
    public ResponseEntity<CardResponse> createCard(Authentication authentication, @Valid @RequestBody CardRequest cardRequest) {
        CardResponse cardResponse = cardService.createCard(userService.getUserByUsername(authentication.getName()), cardRequest.getCurrency());
        return ResponseEntity.ok(cardResponse);
    }

    @PostMapping("/block/{cardId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Заблокировать карту", description = "Блокирует карту для текущего пользователя")
    public ResponseEntity<?> blockCard(@Valid @PathVariable Long cardId) {
        if (cardService.cardExists(cardId)) {
            cardService.blockCard(cardId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Перевод", description = "Перевод между своими картами")
    public ResponseEntity<?> transferCard(@Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            TransactionResponse transaction = transactionService.transferBetweenCards(transactionRequest);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Просмотр баланса",description = "Просмотр баланса карты")
    public ResponseEntity<BigDecimal> getBalance(@Valid @RequestParam String cardNumber) {
        BigDecimal balance = cardService.getCardByNumber(cardNumber).get().getBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/admin/all-cards")
    @Operation(summary = "Посмотреть все карты пользователей", description = "Показывает список всех карт пользователей")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllCards() {
        List<CardResponse> cardResponses = cardService.getAllCards();
        return ResponseEntity.ok(cardResponses);
    }


    @PostMapping("/admin/create/{username}")
    @Operation(summary = "Создать карту для пользователя", description = "Создает новую карту для указанного пользователя")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCardForUser(@Parameter(description = "Имя пользователя") @PathVariable String username, @Valid @RequestBody String currency) {
        try {
            CardResponse cardResponse = cardService.createCard(userService.getUserByUsername(username), currency);
            return ResponseEntity.ok(cardResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/admin/delete/{cardId}")
    @Operation(summary = "Удалить карту", description = "Полностью удаляет карту из системы")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        if (cardService.cardExists(cardId)) {
            cardService.deleteCard(cardId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/admin/update/{cardId}")
    @Operation(summary = "Активировать карту", description = "Активирует выбранную карту")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCard(@PathVariable Long cardId) {
        if (cardService.cardExists(cardId)) {
            cardService.activateCard(cardId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
