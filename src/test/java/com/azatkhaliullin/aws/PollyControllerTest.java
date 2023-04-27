package com.azatkhaliullin.aws;

import com.azatkhaliullin.aws.controller.PollyController;
import com.azatkhaliullin.aws.domain.AmazonPolly;
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

@WebMvcTest(PollyController.class)
public class PollyControllerTest {

    @MockBean
    private AmazonPolly amazonPolly;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void endpointIsUp() throws Exception {
        when(amazonPolly.submitAudio(any(), any()))
                .thenReturn(CompletableFuture.completedFuture(new byte[0]));

        mockMvc.perform(MockMvcRequestBuilders.post("/polly")
                        .param("target", Language.EN.name())
                        .content("test text"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
