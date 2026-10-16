import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VendingMachineItemTest {

    @ParameterizedTest
    @CsvSource({
        "Chips, 1.50",
        "Water, 0.00",
        "Candy, 99.99"
    })
    void constructorStoresNameAndPrice(String name, double price) throws VendingMachineException {
        // Arrange & Act
        VendingMachineItem item = new VendingMachineItem(name, price);

        // Assert
        assertEquals(name, item.getName());
        assertEquals(price, item.getPrice(), 0.000001);
    }

    @Test
    void constructorAllowsZeroPrice() throws VendingMachineException {
        // Arrange & Act
        VendingMachineItem item = new VendingMachineItem("Free sample", 0.0);

        // Assert
        assertEquals("Free sample", item.getName());
        assertEquals(0.0, item.getPrice(), 0.0);
    }

    @ParameterizedTest
    @CsvSource({ "-0.01", "-1.0", "-100.0" })
    void constructorRejectsNegativePrice(double price) {
        // Arrange, Act & Assert
        VendingMachineException exception = assertThrows(
            VendingMachineException.class,
            () -> new VendingMachineItem("Invalid item", price)
        );
        assertEquals("Price cannot be less than zero", exception.getMessage());
    }

    @Test
    void constructorPreservesNullNameBecauseNoNameValidationIsSpecified() throws VendingMachineException {
        // Arrange & Act
        VendingMachineItem item = new VendingMachineItem(null, 2.50);

        // Assert
        assertNull(item.getName());
        assertEquals(2.50, item.getPrice(), 0.000001);
    }
}
