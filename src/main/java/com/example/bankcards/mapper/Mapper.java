package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.RoleEnum;
import org.springframework.stereotype.Component;

/**
 * The type Mapper.
 */
@Component
public class Mapper {

    /**
     * Dto to response card response.
     *
     * @param card the card
     * @return the card response
     */
    public CardResponse dtoToResponse(Card card) {
        return new CardResponse(
                card.getMaskedNumber(),
                card.getUser().getFirstName() + " " + card.getUser().getLastName(),
                card.getCurrency(),
                card.getExpiryDate(),
                card.getStatus().name(),
                card.getBalance(),
                card.isActive(),
                card.isExpired()
        );
    }

    /**
     * Dto to response transaction response.
     *
     * @param transaction the transaction
     * @return the transaction response
     */
    public TransactionResponse dtoToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getFromCard() != null ? transaction.getFromCard().getMaskedNumber() : null,
                transaction.getToCard() != null ? transaction.getToCard().getMaskedNumber() : null,
                transaction.getDescription(),
                transaction.getStatus().name(),
                transaction.getCreatedAt()
        );
    }

    /**
     * Dto to response user response.
     *
     * @param user the user
     * @return the user response
     */
    public UserResponse dtoToResponse(User user) {
        String role = user.getRole().toString();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                role,
                user.getCreatedAt()
        );
    }

    /**
     * Request to dto user.
     *
     * @param request the request
     * @return the user
     */
    public User RequestToDto(UserRequest request){
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .role(RoleEnum.ROLE_USER)
                .build();
    }
}