package com.imageprocessing;

import java.util.Objects;
import java.util.UUID;

public final class PubSubMessage {
    private final String imageName;
    private final UUID processId;

    public PubSubMessage(
        String imageName,
        UUID processId
    ) {
        this.imageName = imageName;
        this.processId = processId;
    }

    public String imageName() {
        return imageName;
    }

    public UUID processId() {
        return processId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (PubSubMessage) obj;
        return Objects.equals(this.imageName, that.imageName) &&
            Objects.equals(this.processId, that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageName, processId);
    }

    @Override
    public String toString() {
        return "PubSubMessage[" +
            "imageName=" + imageName + ", " +
            "processId=" + processId + ']';
    }

}
