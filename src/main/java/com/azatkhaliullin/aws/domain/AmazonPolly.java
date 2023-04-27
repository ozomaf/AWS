package com.azatkhaliullin.aws.domain;

import com.azatkhaliullin.aws.dto.Language;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.polly.model.Voice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Provides text-based speech synthesis using the Amazon Polly.
 */
@Slf4j
@Data
public class AmazonPolly {

    private final ThreadPoolExecutor executorService;
    private Voice voice;

    /**
     * @param executorService thread pool to be used to send pronunciation requests.
     */
    public AmazonPolly(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    /**
     * Obtaining a voice for text dubbing.
     * If the voice has been previously received, it returns the saved voice object, otherwise it queries the Amazon Polly service for a list of votes and returns the first vote from the list.
     *
     * @param pollyClient an instance of the Amazon Polly service client.
     * @param target      the language in which the text is to be dubbed.
     * @return a voice for the text.
     */
    public Voice pollyVoice(PollyClient pollyClient,
                            Language target) {
        if (voice == null) {
            DescribeVoicesRequest request = DescribeVoicesRequest.builder()
                    .engine("standard")
                    .languageCode(target.getAwsPollyValue())
                    .build();
            voice = pollyClient.describeVoices(request).voices().get(0);
        }
        return voice;
    }

    /**
     * Sending an asynchronous text dubbing request to AWS Polly.
     *
     * @param target the language in which the text is to be dubbed.
     * @param text   the text to be dubbed.
     * @return Future object containing an asynchronous text dubbing operation.
     */
    public Future<byte[]> submitAudio(Language target,
                                      String text) {
        return executorService.submit(() -> {
            try (PollyClient client = PollyClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build()) {
                log.debug("AWS Polly client created");
                return synthesizeSpeech(client, target, text);
            } catch (Exception e) {
                log.error("AWS Polly call error", e);
                throw e;
            }
        });
    }

    /**
     * A method that performs speech synthesis based on a given text and language to be voiced.
     *
     * @param pollyClient an instance of the Amazon Polly service client.
     * @param target      the language in which the text is to be dubbed.
     * @param text        the text to be dubbed.
     * @return an array of bytes representing the voiced text in MP3 format.
     * @throws IOException if an error happens when reading from the I/O stream.
     */
    public byte[] synthesizeSpeech(PollyClient pollyClient,
                                   Language target,
                                   String text) throws IOException {
        log.debug("Sending a request to AWS Polly");
        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .languageCode(target.getAwsPollyValue())
                .voiceId(pollyVoice(pollyClient, target).id())
                .outputFormat(OutputFormat.MP3)
                .build();
        log.debug("A request to AWS Polly is formed, {}", request);
        try (ResponseInputStream<SynthesizeSpeechResponse> inputStream = pollyClient.synthesizeSpeech(request)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int readBytes;
            byte[] buffer = new byte[1024];
            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error when sending a request to AWS Polly", e);
            throw e;
        }
    }

}