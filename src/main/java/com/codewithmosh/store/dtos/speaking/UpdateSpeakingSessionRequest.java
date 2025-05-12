/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.support.SessionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpeakingSessionRequest {

    private String topic;
    private String notes;
    private SessionStatus status;
}