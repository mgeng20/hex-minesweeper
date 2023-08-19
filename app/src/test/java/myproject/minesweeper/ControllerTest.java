package myproject.minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
        // Initialize or mock other required fields if necessary
    }

    @Test
    public void testInitialDifficulty() {
        assertEquals(Difficulty.MEDIUM, controller.gameDifficulty);
    }

    @Test
    public void testConvertToMinuteSeconds() {
        assertEquals("00:30", controller.convertToMinuteSeconds(30));
        assertEquals("01:00", controller.convertToMinuteSeconds(60));
        assertEquals("01:30", controller.convertToMinuteSeconds(90));
        assertEquals("10:00", controller.convertToMinuteSeconds(600));
    }

    
    static class MockComboBox {
        String selectedItem;
    
        MockComboBox(String item) {
            this.selectedItem = item;
        }
    
        String getSelectedItem() {
            return selectedItem;
        }
    }
}
