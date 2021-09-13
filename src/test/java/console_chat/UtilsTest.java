package console_chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getPortNumber() {
        assertEquals(1881, Utils.getPortNumber());
    }
}