package hr.fer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hr.fer.common.PuzzleDifficulty;
import hr.fer.common.PuzzleTopic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuzzleTypeInfoDto {
    //TODO: po potrebi deserijalizirati enum

    @JsonProperty("difficulty")
    private PuzzleDifficulty difficulty;
    @JsonProperty("topic")
    private PuzzleTopic topic;
}
