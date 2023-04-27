package com.azatkhaliullin.aws;

import com.azatkhaliullin.aws.controller.TranslateController;
import com.azatkhaliullin.aws.domain.AmazonTranslate;
import com.azatkhaliullin.aws.dto.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(TranslateController.class)
public class TranslateControllerTest {

    @MockBean
    private AmazonTranslate amazonTranslate;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void endpointIsUp() throws Exception {
        when(amazonTranslate.submitTranslation(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(""));

        mockMvc.perform(MockMvcRequestBuilders.post("/translate")
                        .param("source", Language.EN.name())
                        .param("target", Language.RU.name())
                        .content("test text"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}