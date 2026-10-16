import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VendingMachineItemTest {
    /** A set of {@Link VendingMachineItem} initialized before every test */
    VendingMachineItem item1;

    @BeforeEach
    public void setUp() throws Exception {
        item1 = new VendingMachineItem("Coca-Cola", 1);
    }

    @AfterEach
    public void tearDown() throws Exception {
        item1 = null;
    }

    @Test
    public void test_vendingMachineItem_setsValidPrice() {
        assertDoesNotThrow(() -> new VendingMachineItem("Test", 1));
        assertDoesNotThrow(() -> new VendingMachineItem("Test", 0));
    }
    
    @Test
    public void test_vendingMachineItem_throwsInvalidPrice() {
        assertThrows("Price cannot be less than zero", VendingMachineException.class, () -> new VendingMachineItem("Test", -1));
    }
}
