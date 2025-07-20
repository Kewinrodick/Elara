package com.store.elara.dtos;

import com.store.elara.entities.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private ResponseStatus status;
    private String sucessMessage;
    private String errorMessage;
    private Object data;
    private int code;
}
