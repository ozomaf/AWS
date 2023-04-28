package com.azatkhaliullin.aws;

import com.azatkhaliullin.aws.domain.AmazonPolly;
import com.azatkhaliullin.aws.dto.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.polly.model.Voice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AmazonPollyTest {

    private static final Language TEST_LANG = Language.EN;
    private static final String TEST_TEXT = "test text";
    @Autowired
    private AmazonPolly amazonPolly;
    @Mock
    private PollyClient pollyClient;

    @BeforeEach
    public void setUp() {
        when(pollyClient.describeVoices((DescribeVoicesRequest) any()))
                .thenReturn(DescribeVoicesResponse.builder()
                        .voices(Collections.singletonList(Voice.builder().name("testVoice").build()))
                        .build());
        ResponseInputStream<SynthesizeSpeechResponse> inputStream =
                new ResponseInputStream<>(SynthesizeSpeechResponse.builder().build(),
                        new ByteArrayInputStream(new byte[0]));
        when(pollyClient.synthesizeSpeech((SynthesizeSpeechRequest) any()))
                .thenReturn(inputStream);
    }

    @Test
    public void pollyVoice_returnsSavedVoice_whenVoiceIsNotNull() {
        AmazonPolly spyAmazonPolly = Mockito.spy(amazonPolly);

        Voice expectedVoice = Voice.builder().name("testVoice").build();
        spyAmazonPolly.setVoice(expectedVoice);

        Voice actualVoice = spyAmazonPolly.pollyVoice(pollyClient, TEST_LANG);

        assertEquals(expectedVoice, actualVoice);
        verify(spyAmazonPolly).pollyVoice(any(), any());
    }

    @Test
    @DirtiesContext
    public void pollyVoice_queriesAmazonPollyServiceForVoices_whenVoiceIsNull() {
        Voice expectedVoice = Voice.builder().name("testVoice").build();
        List<Voice> voiceList = Collections.singletonList(expectedVoice);

        when(pollyClient.describeVoices((DescribeVoicesRequest) any()))
                .thenReturn(DescribeVoicesResponse.builder().voices(voiceList).build());

        Voice actualVoice = amazonPolly.pollyVoice(pollyClient, TEST_LANG);

        assertEquals(expectedVoice, actualVoice);
        assertEquals(expectedVoice, amazonPolly.getVoice());
        verify(pollyClient).describeVoices((DescribeVoicesRequest) any());
    }

    @Test
    @DirtiesContext
    public void pollyVoice_throwsNullPointerException_whenPollyClientIsNull() {
        assertThrows(NullPointerException.class,
                () -> amazonPolly.pollyVoice(null, TEST_LANG));
    }

    @Test
    public void synthesizeSpeech_returnsByteArray_withValidInputs() throws IOException {
        byte[] bytes = amazonPolly.synthesizeSpeech(pollyClient, Language.EN, TEST_TEXT);
        assertArrayEquals(new byte[0], bytes);
    }

}

