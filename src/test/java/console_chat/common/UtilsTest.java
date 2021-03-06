package console_chat.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getPortNumber() {
        assertEquals(1881, Utils.getPortNumber());
    }

    @Test
    void getServerBacklogSize() {
        assertEquals(100, Utils.getServerBacklogSize());
    }
}