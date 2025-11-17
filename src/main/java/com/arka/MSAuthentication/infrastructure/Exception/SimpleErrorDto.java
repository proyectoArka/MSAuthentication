package com.arka.MSAuthentication.infrastructure.Exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleErrorDto {
    private int status;
    private String message;
}

