package org.tavall.couriers.api.shipping.ai;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tavall.couriers.api.console.Log;
import org.tavall.couriers.api.delivery.state.DeliveryState;
import org.tavall.couriers.api.shipping.helpers.QRShippingLabelCombiner;
import org.tavall.couriers.api.shipping.metadata.ShippingLabelMetaData;
import org.tavall.couriers.api.tracking.TrackingNumberManager;
import org.tavall.couriers.api.tracking.metadata.TrackingNumberMetaData;
import org.tavall.couriers.api.utils.uuid.GenerateUUID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QRShippingLabelCombinerTest {
    private TrackingNumberManager trackingManager = new TrackingNumberManager();
    private QRShippingLabelCombiner combiner;
    private File qrFile;

    private static final String QR_FIXTURE_FILENAME = "qr-9bf52427-b54a-4b87-b970-813f8ca6d8ab.png";

    @TempDir
    Path tempDir;


    @BeforeEach
    public void setUp() {

        combiner = new QRShippingLabelCombiner();

        Path moduleFixture = Path.of(QR_FIXTURE_FILENAME);
        Path rootFixture = Path.of("internal-courier-api", QR_FIXTURE_FILENAME);
        Path fixture = Files.exists(moduleFixture) ? moduleFixture : rootFixture;
        qrFile = fixture.toFile();
        assertTrue(qrFile.isFile(), "QR fixture is missing: " + fixture.toAbsolutePath());
    }


    @Test
    public void createLabelTest() throws IOException {
        // 1. Arrange: "Jane Doe" Data (Safe for screenshots)
        Instant deliverBy = Instant.now().plus(3, ChronoUnit.DAYS);
        GenerateUUID generateUUID = new GenerateUUID();
        String qrUUID = generateUUID.getUUID().toString();
        TrackingNumberMetaData trackingNumber = trackingManager.createTrackingNumber(UUID.fromString(qrUUID));
        ShippingLabelMetaData data = new ShippingLabelMetaData(
                UUID.randomUUID().toString(),
                trackingNumber.trackingNumber(),
                "Jane Doe",
                "(555) 010-9999",
                "404 Null Pointer Rd",
                "Springfield", "IL", "62704", "USA",
                true,
                deliverBy,
                DeliveryState.LABEL_CREATED);

        Path outputPath = tempDir.resolve("shipping-label.pdf");

        // 3. Act
        combiner.createLabel(qrFile.getAbsolutePath(), data, outputPath);

        // 4. Log the location (So you can find it!)
        System.out.println("--------------------------------------------------");
        System.out.println("PDF GENERATED SUCCESSFULLY");
        System.out.println("File Location: " + outputPath.toAbsolutePath());
        System.out.println("--------------------------------------------------");

        // 5. Assert (Still verify it works programmatically)
        assertTrue(Files.exists(outputPath), "The PDF file was not created.");

    }
}
