package dev.surma.parakeeb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LlmSettingsTest {
    @Test
    public void chatCompletionsUrl_appendsV1PathToBaseUrl() {
        LlmSettings settings = new LlmSettings("https://api.openai.com", "key", "model", "");

        assertEquals("https://api.openai.com/v1/chat/completions", settings.chatCompletionsUrl());
    }

    @Test
    public void chatCompletionsUrl_appendsEndpointWhenBaseEndsWithV1() {
        LlmSettings settings = new LlmSettings("https://example.com/v1", "key", "model", "");

        assertEquals("https://example.com/v1/chat/completions", settings.chatCompletionsUrl());
    }

    @Test
    public void chatCompletionsUrl_keepsExplicitEndpoint() {
        LlmSettings settings = new LlmSettings("https://example.com/v1/chat/completions", "key", "model", "");

        assertEquals("https://example.com/v1/chat/completions", settings.chatCompletionsUrl());
    }
}
