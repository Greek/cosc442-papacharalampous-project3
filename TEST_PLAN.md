# Test Plan


| Behavior                    | Valid Case                                      | Invalid Case                                   | Expected Output    |
| --------------------------- | ----------------------------------------------- | ---------------------------------------------- | ------------------ |
| VendingMachineItem          | Price is set when price > 0.                    | Throws when item price is < 0.                 | Void               |
| VendingMachine#addItem      | Add item when slot is free and code is correct. | Throws when slot is taken, or code is invalid. | Void               |
| VendingMachine#removeItem   | Removes item when slot is occupied.             | Throws when slot is already empty.             | VendingMachineItem |
| VendingMachine#makePurchase | Remove item and decrement balance, return true  | Throws when item null or balance < item price  | True               |
| VendingMachine#insertMoney  | Increases balance when > 0 money.               | Throws when money < 1.                         | Void               |