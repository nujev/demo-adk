package agents.multitool;

import static org.junit.jupiter.api.Assertions.*;

import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiToolAgentIntegrationTest {

    private InMemoryRunner runner;
    private Session session;
    private static final String USER_ID = "test-user";
    private static final String SESSION_NAME = "test-session";

    @BeforeEach
    void setUp() {
        runner = new InMemoryRunner(MultiToolAgent.ROOT_AGENT);
        session = runner
            .sessionService()
            .createSession(SESSION_NAME, USER_ID)
            .blockingGet();
    }

    @Test
    void shouldGetCurrentTimeForValidCity() {
        // Given
        String input = "¿Qué hora es en New York?";
        Content userMsg = Content.fromParts(Part.fromText(input));

        // When
        String response = runner.runAsync(USER_ID, session.id(), userMsg)
            .blockingFirst()
            .stringifyContent();

        // Then
        assertTrue(response.contains("The current time in New York is"),
            "La respuesta debe contener la hora actual de New York");
    }

    @Test
    void shouldHandleInvalidCityForTime() {
        // Given
        String input = "¿Qué hora es en CiudadInexistente?";
        Content userMsg = Content.fromParts(Part.fromText(input));

        // When
        String response = runner.runAsync(USER_ID, session.id(), userMsg)
            .blockingFirst()
            .stringifyContent();

        // Then
        assertTrue(response.contains("Sorry, I don't have timezone information for"),
            "La respuesta debe indicar que no se encontró información de la zona horaria");
    }

    @Test
    void shouldGetWeatherForNewYork() {
        // Given
        String input = "¿Qué clima hace en New York?";
        Content userMsg = Content.fromParts(Part.fromText(input));

        // When
        String response = runner.runAsync(USER_ID, session.id(), userMsg)
            .blockingFirst()
            .stringifyContent();

        // Then
        assertTrue(response.contains("The weather in New York is sunny"),
            "La respuesta debe contener información del clima de New York");
    }

    @Test
    void shouldHandleInvalidCityForWeather() {
        // Given
        String input = "¿Qué clima hace en Madrid?";
        Content userMsg = Content.fromParts(Part.fromText(input));

        // When
        String response = runner.runAsync(USER_ID, session.id(), userMsg)
            .blockingFirst()
            .stringifyContent();

        // Then
        assertTrue(response.contains("Weather information for Madrid is not available"),
            "La respuesta debe indicar que no hay información del clima disponible");
    }

    @Test
    void shouldHandleComplexQuestion() {
        // Given
        String input = "¿Qué hora es y qué clima hace en New York?";
        Content userMsg = Content.fromParts(Part.fromText(input));

        // When
        String response = runner.runAsync(USER_ID, session.id(), userMsg)
            .blockingFirst()
            .stringifyContent();

        // Then
        assertTrue(response.contains("New York"),
            "La respuesta debe contener información sobre New York");
        assertTrue(
            response.contains("time") || response.contains("weather"),
            "La respuesta debe contener información sobre el tiempo o el clima");
    }
}
