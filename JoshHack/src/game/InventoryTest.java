package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.awt.Color;

class InventoryTest {

    private Item first;
    private Item second;
    private Item third;

    @BeforeEach
    void setUp() {
        first = new Item('@', Color.WHITE, "First", null);
        second = new Item('#', Color.RED, "Second", null);
        third = new Item('!', Color.BLUE, "Third", null);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5})
    void constructorCreatesInventoryWithRequestedCapacity(int capacity) {
        // Arrange / Act
        Inventory inventory = new Inventory(capacity);

        // Assert
        assertNotNull(inventory.getItems());
        assertEquals(capacity, inventory.getItems().length);
        for (Item item : inventory.getItems()) {
            assertNull(item);
        }
        assertEquals(capacity == 0, inventory.isFull());
    }

    @Test
    void negativeCapacityThrowsNegativeArraySizeException() {
        assertThrows(NegativeArraySizeException.class, () -> new Inventory(-1));
    }

    @ParameterizedTest
    @MethodSource("indexCases")
    void getReturnsItemAtEachValidIndex(int capacity, int index) {
        // Arrange
        Inventory inventory = new Inventory(capacity);
        Item expected = new Item('^', Color.BLUE, "Fourth", null);
        inventory.getItems()[index] = expected;

        // Act
        Item actual = inventory.get(index);

        // Assert
        assertSame(expected, actual);
    }

    static Stream<Arguments> indexCases() {
        return Stream.of(
                Arguments.of(1, 0),
                Arguments.of(3, 0),
                Arguments.of(3, 1),
                Arguments.of(3, 2));
    }

    @ParameterizedTest
    @MethodSource("invalidIndexCases")
    void getRejectsIndexesOutsideArrayBounds(int capacity, int index) {
        Inventory inventory = new Inventory(capacity);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(index));
    }

    static Stream<Arguments> invalidIndexCases() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(2, -1),
                Arguments.of(2, 2));
    }

    @Test
    void addPlacesItemsInFirstAvailableSlotsAndPreservesOrder() {
        // Arrange
        Inventory inventory = new Inventory(3);

        // Act
        inventory.add(first);
        inventory.add(second);
        inventory.add(third);

        // Assert
        assertSame(first, inventory.get(0));
        assertSame(second, inventory.get(1));
        assertSame(third, inventory.get(2));
        assertTrue(inventory.isFull());
        assertTrue(inventory.contains(first));
        assertTrue(inventory.contains(second));
        assertTrue(inventory.contains(third));
    }

    @Test
    void addToFullInventoryDoesNotOverwriteExistingItems() {
        // Arrange
        Inventory inventory = new Inventory(2);
        inventory.add(first);
        inventory.add(second);

        // Act
        inventory.add(third);

        // Assert
        assertSame(first, inventory.get(0));
        assertSame(second, inventory.get(1));
        assertFalse(inventory.contains(third));
        assertTrue(inventory.isFull());
    }

    @Test
    void addingToZeroCapacityInventoryHasNoEffect() {
        // Arrange
        Inventory inventory = new Inventory(0);

        // Act
        inventory.add(first);

        // Assert
        assertEquals(0, inventory.getItems().length);
        assertFalse(inventory.contains(first));
        assertTrue(inventory.isFull());
    }

    @Test
    void removeClearsOnlyTheFirstMatchingReference() {
        // Arrange
        Inventory inventory = new Inventory(3);
        inventory.add(first);
        inventory.add(second);
        inventory.add(first);

        // Act
        inventory.remove(first);

        // Assert
        assertNull(inventory.get(0));
        assertSame(second, inventory.get(1));
        assertSame(first, inventory.get(2));
        assertTrue(inventory.contains(first));
        assertFalse(inventory.isFull());
    }

    @Test
    void removeOfAbsentItemLeavesInventoryUnchanged() {
        // Arrange
        Inventory inventory = new Inventory(2);
        inventory.add(first);
        inventory.add(second);
        Item absent = new Item('^', Color.BLUE, "Fourth", null);;

        // Act
        inventory.remove(absent);

        // Assert
        assertSame(first, inventory.get(0));
        assertSame(second, inventory.get(1));
        assertTrue(inventory.isFull());
    }

    @Test
    void addingAfterRemovalReusesFirstEmptySlotWithoutShiftingItems() {
        // Arrange
        Inventory inventory = new Inventory(3);
        inventory.add(first);
        inventory.add(second);
        inventory.add(third);
        inventory.remove(second);

        // Act
        inventory.add(second);

        // Assert
        assertSame(first, inventory.get(0));
        assertSame(second, inventory.get(1));
        assertSame(third, inventory.get(2));
        assertTrue(inventory.isFull());
    }

    @Test
    void containsUsesReferenceIdentityRatherThanEquals() {
        // Arrange
        Inventory inventory = new Inventory(2);
        Item stored = new Item('^', Color.BLUE, "Fourth", null);;
        Item differentReference = new Item('%', Color.BLUE, "Fifth", null);;
        inventory.add(stored);

        // Act / Assert
        assertTrue(inventory.contains(stored));
        assertFalse(inventory.contains(differentReference));
    }

    @Test
    void isFullChangesAsSlotsAreFilledAndCleared() {
        // Arrange
        Inventory inventory = new Inventory(2);
        assertFalse(inventory.isFull());

        // Act / Assert
        inventory.add(first);
        assertFalse(inventory.isFull());

        inventory.add(second);
        assertTrue(inventory.isFull());

        inventory.remove(first);
        assertFalse(inventory.isFull());
    }

    @Test
    void addNullDoesNotOccupyAnEmptySlot() {
        // Arrange
        Inventory inventory = new Inventory(2);

        // Act
        inventory.add(null);
        inventory.add(first);

        // Assert
        assertSame(first, inventory.get(0));
        assertNull(inventory.get(1));
        assertFalse(inventory.isFull());
    }

    @Test
    void containsNullReflectsTheImplementationOfNullSlots() {
        // This captures current observable behavior: null matches an empty slot.
        Inventory inventory = new Inventory(2);

        assertTrue(inventory.contains(null));
        inventory.add(first);
        assertTrue(inventory.contains(null));
        inventory.add(second);
        assertFalse(inventory.contains(null));
    }

    @Test
    void getItemsReturnsTheBackingArray() {
        // Arrange
        Inventory inventory = new Inventory(1);

        // Act
        inventory.getItems()[0] = first;

        // Assert
        assertSame(first, inventory.get(0));
        assertTrue(inventory.contains(first));
        assertTrue(inventory.isFull());
    }
}
