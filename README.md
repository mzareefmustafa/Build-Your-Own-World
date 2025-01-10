# Build-Your-Own-World

**Build-Your-Own-World** is a simple game where you navigate through a randomly generated world, save your progress, and try to escape by finding the exit door. The world is created using a seed, so it’s different each time you play.

## Features

- **Random World Generation**: A new world every time based on a seed.
- **Move Around**: Use keyboard inputs to explore.
- **Save/Load**: Save your progress and pick up where you left off.
- **Escape**: Find the exit door to win the game!

## Getting Started

1. **Clone the repo**:

   ```bash
   git clone https://github.com/mzareefmustafa/Build-Your-Own-World.git

2. **Go to the project directory**:
3. 
   cd Build-Your-Own-World

4. **Compile the code**:
   
   javac byow/Core/*.java byow/TileEngine/*.java byow/InputDemo/*.java
   
6. **Run the game**:
   
   java byow.Core.Engine
  
## How to Play

- Press N to start a new game. Follow this by typing any number sequence following for the world, then press S to start game.
- Use W, A, S, D to move around.
- Find the exit door to escape and win!

## Saving & Loading

- Save: Your game progress is saved when you press : then Q.
- Load: Press L to load a saved game from savedGame.txt.

## Project Structure

Build-Your-Own-World/
│
├── byow/
│   ├── Core/           # Game logic
│   ├── InputDemo/      # Input handling
│   ├── TileEngine/     # World rendering
│   └── lab12/          # (Optional lab files)
├── README.md           # This file
├── savedGame.txt       # Saved game data
└── .gitignore          # Files to ignore in git
