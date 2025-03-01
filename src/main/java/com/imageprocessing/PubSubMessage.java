package com.imageprocessing;

import java.util.UUID;

public record PubSubMessage(
    String imageName,
    UUID processId
) {
}
