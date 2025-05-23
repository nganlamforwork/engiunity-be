/**
 * Author: lamlevungan
 * Date: 23/04/2025
 **/
package com.codewithmosh.store.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
