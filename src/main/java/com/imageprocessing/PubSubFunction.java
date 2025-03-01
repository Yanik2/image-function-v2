package com.imageprocessing;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;
import com.google.events.cloud.pubsub.v1.Message;
import com.google.events.cloud.pubsub.v1.MessagePublishedData;
import com.google.gson.Gson;
import io.cloudevents.CloudEvent;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class PubSubFunction implements CloudEventsFunction {
    private static final Logger logger = Logger.getLogger(PubSubFunction.class.getName());

    @Override
    public void accept(CloudEvent event) {
        // Get cloud event data as JSON string
        String cloudEventData = new String(event.getData().toBytes());
        // Decode JSON event data to the Pub/Sub MessagePublishedData type
        Gson gson = new Gson();
        MessagePublishedData data = gson.fromJson(cloudEventData, MessagePublishedData.class);
        // Get the message from the data
        Message message = data.getMessage();
        // Get the base64-encoded data from the message & decode it
        String encodedData = message.getData();
        String decodedData = new String(Base64.getDecoder().decode(encodedData));
        // Log the message
        final var pubSubMessage = gson.fromJson(decodedData, PubSubMessage.class);

        final var storage = StorageOptions.getDefaultInstance().getService();
        final var file = storage.get("test-storage-yzin", pubSubMessage.imageName());
        final byte[] content = file.getContent();
        final var convertedImage = convertImage(content);
        storage.create(BlobInfo.newBuilder("test-storage-yzin", pubSubMessage.imageName() + "-processed").build(),
            convertedImage);
        logger.info("Pub/Sub message: " + decodedData);
        logger.info("Image uploaded with name " + pubSubMessage.imageName() + "-processed");
    }

    private byte[] convertImage(byte[] content) {
        try {
            final var image = ImageIO.read(new ByteArrayInputStream(content));

            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    final int rgb = image.getRGB(j, i);
                    final int alpha = (rgb >> 24) & 0xFF;
                    final int red = (rgb >> 16) & 0xFF;
                    final int green = (rgb >> 8) & 0xFF;
                    final int blue = rgb & 0xFF;
                    final int average = (red + green + blue) / 3;
                    final var color = new Color(average, average, average, alpha);
                    image.setRGB(j, i, color.getRGB());
                }
            }

            final var baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}





























