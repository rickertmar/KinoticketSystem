package com.dhbw.kinoticket.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    private String recipient;
    private String body;
    private String subject;
    private String attachment;

}
