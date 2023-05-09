package com.azatkhaliullin.aws.controller;

import com.azatkhaliullin.aws.services.AmazonPolly;
import com.azatkhaliullin.aws.dto.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Controller for processing text-to-speech requests through AWS Polly.
 */
@Slf4j
@RestController
@RequestMapping("/polly")
public class PollyController {

    private static final long DEFAULT_TIMEOUT = 3000; // value in milliseconds
    private final AmazonPolly amazonPolly;

    /**
     * @param amazonPolly an instance of AmazonPolly for making requests to the AWS Polly service.
     */
    public PollyController(AmazonPolly amazonPolly) {
        this.amazonPolly = amazonPolly;
    }

    /**
     * POST endpoint to retrieve voice data from AWS Polly.
     *
     * @param target the language in which the text is to be dubbed.
     * @param text   the text to be dubbed.
     * @return an array of bytes representing the audio data of the converted speech.
     */
    @PostMapping
    public byte[] getVoice(@RequestParam Language target,
                           @RequestBody String text) {
        try {
            return amazonPolly.submitAudio(target, text)
                    .get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Request to AWS Polly service failed", e);
            throw new RuntimeException(e);
        }
    }

}