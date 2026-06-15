package com.email.smart.email.assistant.controller;

import com.email.smart.email.assistant.dto.EmailRequest;
import com.email.smart.email.assistant.service.EmailGeneraterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/email")
public class EmailGeneraterController {

   private final EmailGeneraterService emailGeneraterService;



    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        String response = emailGeneraterService.generateEmail(emailRequest);
        return ResponseEntity.ok(response);
    }

}
