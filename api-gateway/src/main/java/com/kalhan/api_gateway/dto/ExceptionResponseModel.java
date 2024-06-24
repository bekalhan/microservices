package com.kalhan.api_gateway.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionResponseModel {
    private String errCode;
    private String err;
    private String errDetails;
    private String additionalInfo;
    private Date timestamp;
}
