# Challenges and Resolutions

This document summarizes the main challenges encountered while bringing the
`wallet-common` module to a compiling and passing state.

## 1. Large Number of Compilation Errors

The initial test run reported more than 100 compilation errors across the
model, reward, redemption, and wallet tests. Most errors were caused by small
contract mismatches between the tests and the production code:

- Missing JUnit `@Test` imports and AssertJ static imports.
- `Money` constructors receiving primitive values instead of `BigDecimal`.
- AssertJ method names and exception assertion patterns that did not match the
  available APIs.
- Test references to method names that differed from the implementation.

### Resolution

The tests were aligned with the existing domain model and library APIs:

- Added the required JUnit and AssertJ imports.
- Used `BigDecimal.valueOf(...)` for monetary values.
- Captured exceptions with `assertThrows(...)` and asserted their messages
  separately.
- Replaced `isEqualToByComparingTo` with AssertJ's
  `isEqualByComparingTo`.
- Standardized `Points.isAtLeast()` and `Wallet.enrollProduct()` naming.

## 2. Missing Domain Accessors

The redemption state records stored timestamps, but the tests needed explicit
accessors such as `requestedAt()`, `approvedAt()`, and `settledAt()`.

### Resolution

Accessors were added to each relevant `RedemptionState` record. The
`Requested` state also received an `of(Instant)` factory method so all states
could be created consistently.

## 3. Immutable Wallet State Transition

`Wallet` is immutable. Its `close(...)` operation returns a new closed wallet
instead of changing the existing instance. The test initially called the
method without storing its return value, so it continued checking the original
active wallet.

### Resolution

The test now captures the returned value:

```java
Wallet closedWallet = wallet.close(closedAt, reason);
```

Assertions are made against `closedWallet`, while the original wallet remains
unchanged as required by the immutable aggregate design.

## 4. Validation Order in Reward Factories

The typed reward records enforce their own point ranges. However, the factory
methods initially called `Points.of(...)` first. For example,
`microReward(-1)` produced a generic negative-points error before the more
useful micro-reward range error could be reported.

### Resolution

Each reward factory now validates its domain range before constructing a
`Points` value:

- Micro: `[2, 19]`
- Small: `[20, 99]`
- Medium: `[100, 499]`
- Large: `[500, 1999]`
- Jackpot: `[2000, 10000]`

This preserves the most specific validation message for callers and keeps the
record constructors as a second line of defense.

## 5. Annual Cap Semantics

`ClippedByAnnualCap` represents an award that was reduced by an annual limit.
The awarded amount therefore cannot be greater than the amount that would have
been awarded without the cap.

### Resolution

The record now validates that:

```text
awarded <= wouldHaveBeen
```

It also validates that both point values remain within the supported range.

## 6. Error Message Alignment

One test expected the phrase "between 0.0 and 5.0", while the implementation
reported the equivalent and more precise phrase "in the range [0.0, 5.0]".

### Resolution

The test assertion was updated to match the implementation's actual public
error message while still checking the important range information.

## Verification

The final command was:

```bash
./mvnw -pl wallet-common clean test
```

Result: **48 tests run, 0 failures, 0 errors, 0 skipped**.

The `wallet-common` module now compiles cleanly and its complete test suite
passes.