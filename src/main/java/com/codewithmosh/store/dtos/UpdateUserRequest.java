/**
 * Author: lamlevungan
 * Date: 20/04/2025
 **/
package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
}
