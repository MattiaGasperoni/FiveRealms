# FiveRealms: Java Adventure RPG

A desktop adventure role-playing game built in **Java**. Explore five distinct realms, engage in strategic battles, recruit and manage a party of allies, and persist game progress through a built-in save/load system.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture & Core Systems](#architecture--core-systems)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Building & Running](#building--running)
- [Documentation](#documentation)

---

## Overview

**FiveRealms** is an object-oriented desktop RPG designed around progressive world exploration and tactical party combat. Players traverse five interconnected realms—each featuring unique environments, enemy encounters, and quests—while leveling up their protagonist, assembling a team of allies, and uncovering the storyline threatening the kingdom.

---

## Features

* **Multi-Realm World Design**: Five distinct realms, each introducing unique environments, enemy archetypes, and progression challenges.
* **Character Progression**: Experience and leveling mechanics that scale player statistics and combat capabilities.
* **Party Management System**: Recruit, organize, and deploy a roster of allies with complementary roles in battle.
* **Strategic Combat Engine**: Real-time battle system supporting standard actions, special abilities, and tactical party coordination.
* **Save / Load Persistence**: Full game-state serialization allowing players to save and resume campaigns at any point.
* **Integrated Audio Engine**: Background soundtrack and event-driven sound effects synchronized with gameplay states.

---

## Architecture & Core Systems

The codebase follows object-oriented design principles to decouple game state, rendering, audio playback, and persistence:

* **Game Loop & State Management**: Coordinates real-time updates, realm transitions, and encounter triggers.
* **Entity & Party Hierarchy**: Polymorphic modeling of playable characters, recruitable allies, NPCs, and enemy units.
* **Combat & Progression Subsystem**: Encapsulates damage calculation, special attack cooldowns/resources, and leveling curves.
* **Persistence Layer**: Handles serialization and deserialization of player stats, party composition, and world progress.
* **Audio Controller**: Manages background music loops and sound effect playback across different realms and battle states.

---

## Getting Started

### Prerequisites
- **Java Development Kit (JDK)** 11 or higher installed and available in your `PATH`.

### Building & Running

1. Clone the repository:
   ```bash
   git clone https://github.com/mattiagasperoni/FiveRealms.git
   cd FiveRealms
   ```

2. Compile the Java source files (adjust source/output paths if using Maven/Gradle or an IDE project layout):
   ```bash
   javac -d bin $(find src -name "*.java")
   ```

3. Run the application:
   ```bash
   java -cp bin Main
   ```
   *(Replace `Main` with the main entry-point class if located inside a named package).*

---

## Documentation

For detailed UML class diagrams, software architecture breakdowns, gameplay mechanics, and design choices, refer to the technical report included in the repository.
