package com.azatkhaliullin.aws;

import com.azatkhaliullin.aws.domain.AmazonTranslate;
import com.azatkhaliullin.aws.dto.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AmazonTranslateTest {
    private static final Language TEST_SRC_LANG = Language.EN;
    private static final Language TEST_TG_LANG = Language.EN;
    private static final String TEST_TEXT = "test text";

    @Autowired
    private AmazonTranslate amazonTranslate;
    @Mock
    private TranslateClient translateClient;

    @BeforeEach
    public void setUp() {
        TranslateTextResponse response = TranslateTextResponse.builder()
                .translatedText(TEST_TEXT)
                .build();
        when(translateClient.translateText((TranslateTextRequest) any())).thenReturn(response);
    }

    @Test
    public void translateText_returnsTranslatedText_whenRequestIsValid() {
        String actionTranslation = amazonTranslate.translateText(translateClient, TEST_SRC_LANG, TEST_TG_LANG, TEST_TEXT);

        assertEquals(TEST_TEXT, actionTranslation);
    }

    @Test
    public void translateText_withException() {
        when(translateClient.translateText(any(TranslateTextRequest.class)))
                .thenThrow(new RuntimeException());

        assertThrows(Exception.class,
                () -> amazonTranslate.translateText(translateClient, TEST_SRC_LANG, TEST_TG_LANG, TEST_TEXT));
    }

}
