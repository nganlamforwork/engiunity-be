/**
 * Author: lamlevungan
 * Date: 21/04/2025
 **/
package com.codewithmosh.store.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
