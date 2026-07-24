package org.tavall.couriers.web.view;

import org.junit.jupiter.api.Test;
import org.tavall.couriers.api.qr.scan.state.CameraState;
import org.tavall.couriers.api.qr.scan.state.GeminiResponseState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeminiVisionServiceTest {

    @Test
    void analyzeFrameRejectsEmptyDataWithoutCreatingAnExternalClient() {
        GeminiVisionService service = new GeminiVisionService();

        var result = service.analyzeFrame(new byte[0], true);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(CameraState.ERROR, result.getResponse().cameraState());
        assertEquals(GeminiResponseState.ERROR, result.getResponse().geminiResponseState());
        assertEquals("Empty Frame Data", result.getResponse().notes());
    }
}
