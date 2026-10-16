import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class VendingMachineTest {
    private VendingMachine machine;

    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
    }

    @Test
    void startsWithZeroBalanceAndAllSlotsEmpty() throws VendingMachineException {
        // Arrange is handled by setUp; Act
        // Assert
        assertEquals(0.0, machine.getBalance(), 0.0);
        assertNull(machine.getItem(VendingMachine.A_CODE));
        assertNull(machine.getItem(VendingMachine.B_CODE));
        assertNull(machine.getItem(VendingMachine.C_CODE));
        assertNull(machine.getItem(VendingMachine.D_CODE));
    }

    @ParameterizedTest
    @CsvSource({
        "A, 0",
        "B, 1",
        "C, 2",
        "D, 3"
    })
    void addItemPlacesItemInRequestedSlot(String code, int unusedIndex) throws VendingMachineException {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Snack", 1.25);

        // Act
        machine.addItem(item, code);

        // Assert
        assertSame(item, machine.getItem(code));
    }

    @ParameterizedTest
    @ValueSource(strings = { "E", "a", "", "AA" })
    void addItemRejectsInvalidSlotCode(String code) throws VendingMachineException {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Snack", 1.00);

        // Act & Assert
        VendingMachineException exception = assertThrows(
            VendingMachineException.class, () -> machine.addItem(item, code));
        assertEquals("Invalid code for vending machine item", exception.getMessage());
        assertEquals(0.0, machine.getBalance(), 0.0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void addingToOccupiedSlotThrowsAndPreservesOriginalItem(String code) throws VendingMachineException {
        // Arrange
        VendingMachineItem original = new VendingMachineItem("Original", 1.00);
        VendingMachineItem replacement = new VendingMachineItem("Replacement", 2.00);
        machine.addItem(original, code);

        // Act
        VendingMachineException exception = assertThrows(
            VendingMachineException.class, () -> machine.addItem(replacement, code));

        // Assert
        assertEquals("Slot " + code + " already occupied", exception.getMessage());
        assertSame(original, machine.getItem(code));
    }

    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void removeItemReturnsItemAndEmptiesSlot(String code) throws VendingMachineException {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Snack", 1.25);
        machine.addItem(item, code);

        // Act
        VendingMachineItem removed = machine.removeItem(code);

        // Assert
        assertSame(item, removed);
        assertNull(machine.getItem(code));
    }

    @Test
    void removingEmptySlotThrowsHelpfulException() {
        // Arrange & Act
        VendingMachineException exception = assertThrows(
            VendingMachineException.class, () -> machine.removeItem("C"));

        // Assert
        assertEquals("Slot C is empty -- cannot remove item", exception.getMessage());
        assertEquals(0.0, machine.getBalance(), 0.0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "E", "a", "" })
    void removingWithInvalidCodeThrows(String code) {
        // Arrange, Act & Assert
        VendingMachineException exception = assertThrows(
            VendingMachineException.class, () -> machine.removeItem(code));
        assertEquals("Invalid code for vending machine item", exception.getMessage());
    }

    @Test
    void insertMoneyAddsAmountToExistingBalance() throws VendingMachineException {
        // Arrange & Act
        machine.insertMoney(2.00);
        machine.insertMoney(1.25);

        // Assert
        assertEquals(3.25, machine.getBalance(), 0.000001);
    }

    @ParameterizedTest
    @CsvSource({
        "-1.0, Invalid amount.  Amount must be >= 0",
        "0.0, Invalid amount.  Amount must be >= 0",
        "0.99, Invalid amount.  Amount must be >= 0"
    })
    void insertMoneyRejectsAmountsBelowOneInCurrentImplementation(
            double amount, String expectedMessage) throws VendingMachineException {
        // Note: this captures current implementation behavior. The Javadoc says
        // amounts >= 0 are valid, but the code rejects every amount < 1.
        // Arrange, Act & Assert
        VendingMachineException exception = assertThrows(
            VendingMachineException.class, () -> machine.insertMoney(amount));
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(0.0, machine.getBalance(), 0.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 1.0, 1.25, 10.0 })
    void insertMoneyAcceptsAmountsAtLeastOne(double amount) throws VendingMachineException {
        // Arrange & Act
        machine.insertMoney(amount);

        // Assert
        assertEquals(amount, machine.getBalance(), 0.000001);
    }

    @Test
    void getBalanceDoesNotChangeBalance() throws VendingMachineException {
        // Arrange
        machine.insertMoney(4.50);

        // Act
        double firstRead = machine.getBalance();
        double secondRead = machine.getBalance();

        // Assert
        assertEquals(4.50, firstRead, 0.000001);
        assertEquals(firstRead, secondRead, 0.0);
    }

    @Test
    void purchaseWithInsufficientFundsReturnsFalseAndPreservesItemAndBalance()
            throws VendingMachineException {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Lunch", 5.00);
        machine.addItem(item, "A");
        machine.insertMoney(2.00);

        // Act
        boolean purchased = machine.makePurchase("A");

        // Assert
        assertFalse(purchased);
        assertSame(item, machine.getItem("A"));
        assertEquals(2.00, machine.getBalance(), 0.000001);
    }

    @Test
    void purchaseWithExactFundsSucceedsAndEmptiesSlot() throws VendingMachineException {
        // Arrange
        VendingMachineItem item = new VendingMachineItem("Lunch", 2.50);
        machine.addItem(item, "B");
        machine.insertMoney(2.50);

        // Act
        boolean purchased = machine.makePurchase("B");

        // Assert
        assertTrue(purchased);
        assertNull(machine.getItem("B"));
        assertEquals(0.0, machine.getBalance(), 0.000001);
    }

    @Test
    void purchaseLeavesChangeAfterBuyingItem() throws VendingMachineException {
        // Arrange
        machine.addItem(new VendingMachineItem("Snack", 1.75), "D");
        machine.insertMoney(5.00);

        // Act
        boolean purchased = machine.makePurchase("D");

        // Assert
        assertTrue(purchased);
        assertEquals(3.25, machine.getBalance(), 0.000001);
        assertNull(machine.getItem("D"));
    }

    @Test
    void purchaseFromEmptySlotReturnsFalseWithoutChangingBalance() throws VendingMachineException {
        // Arrange
        machine.insertMoney(3.00);

        // Act
        boolean purchased = machine.makePurchase("A");

        // Assert
        assertFalse(purchased);
        assertEquals(3.00, machine.getBalance(), 0.000001);
        assertNull(machine.getItem("A"));
    }

    @Test
    void zeroPriceItemCanBePurchasedWithoutReducingBalance() throws VendingMachineException {
        // Arrange
        VendingMachineItem freeItem = new VendingMachineItem("Free", 0.0);
        machine.addItem(freeItem, "C");
        machine.insertMoney(1.00);

        // Act
        boolean purchased = machine.makePurchase("C");

        // Assert
        assertTrue(purchased);
        assertNull(machine.getItem("C"));
        assertEquals(1.00, machine.getBalance(), 0.000001);
    }

    @Test
    void purchaseWithInvalidCodeThrowsException() {
        // Arrange, Act & Assert
        assertThrows(VendingMachineException.class, () -> machine.makePurchase("Z"));
    }

    @Test
    void returnChangeReturnsEntireBalanceAndResetsItToZero() throws VendingMachineException {
        // Arrange
        machine.insertMoney(2.00);
        machine.insertMoney(3.50);

        // Act
        double change = machine.returnChange();

        // Assert
        assertEquals(5.50, change, 0.000001);
        assertEquals(0.0, machine.getBalance(), 0.0);
    }

    @Test
    void returningChangeFromEmptyMachineReturnsZeroAndKeepsBalanceZero() {
        // Arrange & Act
        double change = machine.returnChange();

        // Assert
        assertEquals(0.0, change, 0.0);
        assertEquals(0.0, machine.getBalance(), 0.0);
    }
}
