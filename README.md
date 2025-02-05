# 🌍 Build Your Own World (BYoW)  

A **randomly generated, explorable world** built in Java! Navigate through procedurally generated landscapes, save your progress, and find the exit to escape.  

## 🚀 Features  

- **🗺️ Random World Generation** – Every playthrough is unique with seed-based generation.  
- **🎮 Player Movement** – Use **W, A, S, D** to explore the world.  
- **💾 Save & Load** – Quit anytime and resume exactly where you left off.  
- **🚪 Find the Exit** – Discover the exit door to win the game!  
- **📜 HUD & Interaction** – Hover over tiles to get descriptions.  

## 🛠 Getting Started  

### 1️⃣ Clone the Repository  
```sh
git clone https://github.com/mzareefmustafa/Build-Your-Own-World.git  
cd Build-Your-Own-World  
```

### 2️⃣ Compile the Code  
```sh
javac byow/Core/*.java byow/TileEngine/*.java byow/InputDemo/*.java
```

### 3️⃣ Run the Game 
```sh
java byow.Core.Engine 
```

## 🎮 How to Play  

| Command          | Action                               |
|------------------|--------------------------------------|
| `N + [seed] + S` | Start a new game with a custom seed. |
| `W, A, S, D`     | Move around the world.               |
| `:Q`             | Save and quit.                       |
| `L`              | Load the last saved game.            |

## 📂 Project Structure

Build-Your-Own-World/
│
├── byow/
│'''├── Core/           # Main game logic  
│'''├── InputDemo/      # Handles user input  
│'''├── TileEngine/     # World rendering engine  
│
├── README.md           # Project documentation  
├── savedGame.txt       # Stores saved game data  
└── .gitignore          # Files to exclude from version control  
 
