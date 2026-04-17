# BookMyStayApp

This project presents the design and implementation of a Hotel Booking Management System to illustrate the practical application of Core Java and fundamental data structures in real-world scenarios. The system is developed incrementally, with each use case introducing a specific concept that addresses common software engineering challenges such as fair request handling, inventory consistency, and prevention of double-booking. By focusing on core logic and system behavior rather than user interface concerns, the project enables learners to understand not only how data structures are used, but why they are essential in scalable and maintainable software systems.


Use Case 12: Data Persistence & System Recovery
-

**Goal:** 

Introduce persistence and recovery concepts by ensuring that critical system state survives application restarts, transitioning learners from in-memory thinking to durable system design.

**Actor:**

System – initiates save and restore operations during shutdown and startup.

Persistence Service – handles storing and retrieving system state from persistent storage.

**Flow:**

The system prepares for shutdown.

Current booking and inventory state is serialized into a persistent format.

Serialized data is written to a file.

System restarts.

Persisted data is loaded from the file.

Inventory and booking state are restored into memory.

System resumes operation with recovered state.

**Key Concepts Used**

Stateful Applications - A stateful application maintains data beyond a single execution cycle. Business systems must preserve state to ensure continuity and correctness.

Persistence - Persistence refers to storing application state in a durable medium. This prevents data loss caused by restarts, crashes, or redeployments.

Serialization - Serialization converts in-memory objects into a format suitable for storage. This allows complex data structures to be written to files and later reconstructed.

Deserialization - Deserialization restores objects from persisted data back into memory. Correct deserialization is essential for accurate system recovery.

Inventory Snapshot - The inventory state is captured at a point in time. Restoring this snapshot ensures availability reflects the last known valid state.

Failure Tolerance - The system handles missing or corrupted persistence data safely. This prevents crashes and allows the application to start in a known, valid state.

Preparation for Database Integration - File-based persistence introduces durability concepts without database complexity. This prepares learners conceptually for future database-backed systems.

**Key Requirements**

Persist booking history and inventory state to a file.

Restore persisted data during application startup.

Ensure the restored state accurately reflects the last saved state.

Handle missing or corrupted persistence files gracefully.

Allow the system to continue operating safely after recovery.

**Key Benefits**

No data loss across application restarts

More realistic and production-aligned system behavior

Smooth conceptual transition toward database-backed systems

**Drawbacks of Previous Use Case**

Earlier use cases relied entirely on in-memory data structures.
As a result, all business state was lost when the application terminated.