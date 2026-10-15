import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * VendingMachineTest is a test suite to test {@link VendingMachine}'s core logic.
 */
public class VendingMachineTest {
    VendingMachine machine1, machine2, machine3;

    @BeforeEach 
    public void setUp() {
        machine1 = new VendingMachine();
        machine1.addItem(new VendingMachineItem("Peanuts", 1), "A");
        machine1.addItem(new VendingMachineItem("Coca-Cola", 2), "B");
        machine1.addItem(new VendingMachineItem("Noodles", 4), "C");
        machine1.addItem(new VendingMachineItem("Apple Juice", 2), "D");
        machine2 =  new VendingMachine();
        machine2.addItem(new VendingMachineItem("Peanuts", 1), "A");
        machine2.addItem(new VendingMachineItem("Coca-Cola", 2), "B");
        machine2.addItem(new VendingMachineItem("Noodles", 4), "C");
        machine2.addItem(new VendingMachineItem("Apple Juice", 2), "D");
        machine3 = new VendingMachine();
        machine3.addItem(new VendingMachineItem("Peanuts", 1), "A");
        machine3.addItem(new VendingMachineItem("Coca-Cola", 2), "B");
    }

    @AfterEach 
    public void tearDown() {
        machine1 = null;
        machine2 = null;
        machine3 = null;
    }

    @Test()
    public void test_addItem_successfullyAddsItemSuccessfullyWhenSlotIsFree() {
        Executable call = () -> machine3.addItem(new VendingMachineItem("Item1", 1), "C");
        assertDoesNotThrow(call);
        assertEquals("Item1", machine3.getItem("C").getName());
    }

    @Test()
    public void test_addItem_throwsWhenSlotIsOccupied() {
        ThrowingRunnable call = () -> machine3.addItem(new VendingMachineItem("Item1", 1), "B");
        assertThrows("Slot B already occupied", VendingMachineException.class, call);
    }

    @Test()
    public void test_addItem_returnsInvalidCode() {
        ThrowingRunnable call = () -> machine3.addItem(new VendingMachineItem("Item1", 1), "E");
        assertThrows("Invalid code for vending machine item", VendingMachineException.class, call);
    }

    @Test()
    public void test_removeItem_successfullyRemovesItemWhenSlotIsOccupied() {
        Executable call = () -> machine3.removeItem("B");
        assertDoesNotThrow(call);
        assertEquals(null, machine3.getItem("B"));
    }
    
    @Test()
    public void test_removeItem_throwWhenSlotIsAlreadyUnoccupied() {
        ThrowingRunnable call = () -> machine3.removeItem("C");
        assertThrows("Slot C is empty -- cannot remove item", VendingMachineException.class, call);
    }

    @Test()
    public void test_makePurchase_removeItemAndDecrement() {
        machine3.insertMoney(4);
        boolean val = machine3.makePurchase("B");
        assertNull(machine3.getItem("B"));
        assertEquals(2, machine3.balance);
        assertEquals(true, val);
    }
    
    @Test()
    public void test_makePurchase_throwWhenItemNull() {
        int initialBal = 4;
        machine3.insertMoney(initialBal);
        boolean val = machine3.makePurchase("C");
        assertEquals(initialBal, machine3.balance);
        assertEquals(false, val);
    }
    
    @Test()
    public void test_makePurchase_throwWhenBalanceLessThanItemPrice() {
        int initialBal = 1;
        machine3.insertMoney(initialBal);
        boolean val = machine3.makePurchase("B");
        assertEquals(initialBal, machine3.balance);
        assertEquals(false, val);
    }
    
    @Test()
    public void test_insertMoney_increasesBalanceWhenValueIsMoreThanZero() {
        int initialBal = 1;
        machine3.insertMoney(initialBal);
        assertEquals(initialBal, machine3.balance);
    }
    
    @Test()
    public void test_insertMoney_topsUpBalance() {
        int initialBal = 1;
        machine3.insertMoney(initialBal);
        assertEquals(initialBal, machine3.balance);
        machine3.insertMoney(4);
        assertEquals(5, machine3.balance);
    }
    
    @Test()
    public void test_insertMoney_throwsWhenValueIsZeroOrLess() {
        int initialBal = 0;
        ThrowingRunnable call = () -> machine3.insertMoney(initialBal);
        ThrowingRunnable call2 = () -> machine3.insertMoney(-2);

        assertThrows("Invalid amount.  Amount must be >= 0", VendingMachineException.class, call);
        assertThrows("Invalid amount.  Amount must be >= 0", VendingMachineException.class, call2);
    }
}
