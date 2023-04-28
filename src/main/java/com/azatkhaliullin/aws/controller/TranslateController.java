package com.azatkhaliullin.aws.controller;

import com.azatkhaliullin.aws.domain.AmazonTranslate;
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
 * Controller for processing text translation requests through AWS Translate.
 */
@Slf4j
@RestController
@RequestMapping("/translate")
public class TranslateController {

    // константы обычно объявляются выше переменных
    private static final long DEFAULT_TIMEOUT = 3000; // value in milliseconds

    private final AmazonTranslate amazonTranslate;

    /**
     * @param amazonTranslate an instance of AmazonTranslate for making requests to the AWS Translate service.
     */
    public TranslateController(AmazonTranslate amazonTranslate) {
        this.amazonTranslate = amazonTranslate;
    }

    /**
     * POST endpoint to retrieve text translation from AWS Translate service.
     *
     * @param source the language from which the translation is made.
     * @param target the language into which the translation is being made.
     * @param text   the text to be translated.
     * @return the translated text.
     * @throws RuntimeException if the translation request fails or times out.
     */
    @PostMapping
    public String getTranslate(@RequestParam Language source,
                               @RequestParam Language target,
                               @RequestBody String text) {
        try {
            return amazonTranslate.submitTranslation(source, target, text)
                    .get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Request to AWS Translate service failed", e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("Exceeded query time for AWS Translate service", e);
            throw new RuntimeException(e);
        }
    }

}