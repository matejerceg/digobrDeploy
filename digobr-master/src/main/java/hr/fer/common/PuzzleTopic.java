package hr.fer.common;

public enum PuzzleTopic {
    GENERAL(1, "general"),
    SPORT(2, "sport"),
    HISTORY(3, "povijest");

    private final int topicNumber;
    private final String topicName;

    PuzzleTopic(int topicNumber, String topicName) {
        this.topicNumber = topicNumber;
        this.topicName = topicName;
    }

    public int getTopicNumber() {
        return topicNumber;
    }

    public String getTopicName() {
        return topicName;
    }
}
