package hr.fer.controller;

import hr.fer.common.ApiPaths;
import hr.fer.common.OpenAIRequestConstants;
import hr.fer.dto.PuzzleDto;
import hr.fer.dto.PuzzleTypeInfoDto;
import hr.fer.dto.openai.ChatGPTRequest;
import hr.fer.dto.openai.ChatGPTResponse;
import hr.fer.services.PuzzleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
public class ChatGPTController {

    @Autowired
    private RestTemplate restTemplate;

    private final PuzzleService puzzleService;
    private final OpenAIRequestConstants openAIRequestConstants;

    public ChatGPTController(PuzzleService puzzleService, OpenAIRequestConstants openAIRequestConstants) {
        this.puzzleService = puzzleService;
        this.openAIRequestConstants = openAIRequestConstants;
    }

    @PostMapping(ApiPaths.GENERATE_PUZZLE)
    public PuzzleDto generatePuzzle(@RequestBody PuzzleTypeInfoDto puzzleTypeInfo) {
        String prompt = puzzleService.createPrompt(puzzleTypeInfo);

        ChatGPTRequest request = new ChatGPTRequest(openAIRequestConstants.MODEL, prompt);
        ChatGPTResponse response = restTemplate.postForObject(openAIRequestConstants.API_URL, request, ChatGPTResponse.class);

        PuzzleDto puzzle = puzzleService.createPuzzle(response);
        return puzzle;
    }
}
