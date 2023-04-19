package com.azatkhaliullin.aws.domain;

import com.azatkhaliullin.aws.dto.Language;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Provides text translation using Amazon Translate.
 */
@Slf4j
public class AmazonTranslate {

    private final ThreadPoolExecutor executorService;

    /**
     * @param executorService the thread pool to be used to send text translation requests.
     */
    public AmazonTranslate(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    /**
     * A method that translates text using the Amazon Translate service.
     *
     * @param translateClient an instance of the Amazon Translate service client.
     * @param source          the language from which the translation is made.
     * @param target          the language into which the translation is being made.
     * @param text            the text to be translated.
     * @return string with the translated text.
     */
    public String translateText(TranslateClient translateClient,
                                Language source,
                                Language target,
                                String text) {
        TranslateTextRequest request = TranslateTextRequest.builder()
                .sourceLanguageCode(source.getAwsTranslateValue())
                .targetLanguageCode(target.getAwsTranslateValue())
                .text(text)
                .build();
        TranslateTextResponse response = translateClient.translateText(request);
        return response.translatedText();
    }

    /**
     * A method that sends a text translation request to the executorService thread pool.
     *
     * @param source the language from which the translation is made.
     * @param target the language into which the translation is being made.
     * @param text   the text to be translated.
     * @return Future<String> object that contains the result of the translation.
     */
    public Future<String> submitTranslation(Language source,
                                            Language target,
                                            String text) {
        return executorService.submit(() -> {
            try (TranslateClient client = TranslateClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build()) {
                log.debug("AWS Translate client created");
                return translateText(client, source, target, text);
            } catch (Exception e) {
                log.error("AWS Translate call error", e);
                throw e;
            }
        });
    }

}