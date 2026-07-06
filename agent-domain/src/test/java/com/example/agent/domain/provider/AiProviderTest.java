package com.example.agent.domain.provider;

import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiProviderTest {

    @Test
    void shouldParseProviderFromName() {
        assertEquals(AiProvider.SPRING_AI, AiProvider.valueOf("SPRING_AI"));
        assertEquals(AiProvider.LANGCHAIN4J, AiProvider.valueOf("LANGCHAIN4J"));
    }
}
