# Bugs

---

First bug: VendingMachine constructor had faulty for-loop for initializing
slots. `i <= NUM_SLOTS` was corrected to `i < NUM_SLOTS`, since indexes
start at 0, and not at 1.
