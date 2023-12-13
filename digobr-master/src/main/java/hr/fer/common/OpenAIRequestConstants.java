package hr.fer.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIRequestConstants {

    @Value("${openai.model}")
    public String MODEL;

    @Value("${openai.api.url}")
    public String API_URL;
}
