package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.RoleEnum;
import com.example.bankcards.util.RoleSubset;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Schema(description = "Логин", example = "Kkjss90")
    @Size(min = 5, max = 50, message = "Логин должен содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "example@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;

    @Schema(description = "Имя", example = "Name")
    @NotBlank
    @Size(max = 50, message = "Имя должно содержать до 50 символов")
    private String firstName;

    @Schema(description = "Фамилия", example = "LastName")
    @NotBlank
    @Size(max = 50, message = "Фамилия должна содержать до 50 символов")
    private String lastName;



    @NotNull(message = "Роль не может быть null")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Роль", example = "ROLE_USER, ROLE_ADMIN")
    @RoleSubset(anyOf = {RoleEnum.ROLE_USER, RoleEnum.ROLE_ADMIN})
    private RoleEnum role;

}
