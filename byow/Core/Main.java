package byow.Core;

/**
 * This is the main entry point for the program. This class parses
 * the command line inputs and delegates execution to the Engine class
 * for either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            // Exit if there are too many arguments
            System.out.println("Error: Can only have one argument - the input string.");
            System.exit(0);
        } else if (args.length == 1) {
            // Input string mode
            Engine engine = new Engine();
            engine.interactWithInputString(args[0]);
            // Print the result for testing or verification
            System.out.println(engine.toString()); // Ensure Engine.toString() is meaningful
        } else {
            // Keyboard mode
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
