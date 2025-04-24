/**
 * Author: lamlevungan
 * Date: 22/04/2025
 **/
package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
}
