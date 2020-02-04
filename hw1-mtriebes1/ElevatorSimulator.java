/**
 * Author: Matthew Triebes
 * Course: CPSC 326, Spring 2020
 * Assignment: HW 1 - ElevatorSimulator.java
 * 
 * Description: Simple program for practicing reading from a file,
 * parsing its contents, and printing the results.
 */

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;


public class ElevatorSimulator {

  private BufferedReader buffer;           // buffered input stream reader
  private int elevatorCount;               // number of elevators
  private int[] elevatorFloors;
  //private Vector<Integer> elevatorFloors;  // list of elevator locations

  /** 
   * Create a new elevator simulator
   */
  public ElevatorSimulator(InputStream inStream)
  {
    this.elevatorCount = 0;
    this.elevatorFloors = new int[elevatorCount];
    this.buffer = new BufferedReader(new InputStreamReader(inStream));
  }

  /**
   * Returns next character in the stream. Gives -1 if end of file.
   */
  private int read() {
    try {
      int ch = buffer.read();
      return ch;
    } catch(IOException e) {
      error("read error");
    }
    return -1;
  }

  /** 
   * Returns next character without removing it from the stream.
   */
  private int peek() {
    int ch = -1;
    try {
      buffer.mark(1);
      ch = read();
      buffer.reset();
    } catch(IOException e) {
      error("read error");
    }
    return ch;
  }

  /**
   * Read a sequence of white space characters.
   */
  private void readSpace() {
    int ch = peek();
    while (Character.isWhitespace(ch) && ch != -1) {
      read();
      ch = peek();
    }
  }

  /**
   * Read and return a sequence of characters (up to whitespace).
   */
  private String readString() {
    String str = "";
    int ch = peek();
    while(!Character.isWhitespace(ch) && ch != -1) {
      str += (char)read();
      ch = peek();
    }
    return str;
  }

  /**
   * Read a sequence of characters (digits) and return as an integer
   * value.
   */ 
  private int readInt() {
    String str = "";
    int ch = peek();
    while (!Character.isWhitespace(ch) && ch != -1) {
      str += (char)read();
      ch = peek();
    }
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException e) {
      error("expecting integer, found '" + str + "'");
    }
    return -1;
  }

  /**
   * Print an error message and exit the program.
   */
  private void error(String msg) {
    System.out.println("Error: " + msg);
    System.exit(1);
  }

  /** 
   * Builds and runs the simulation
   */ 
  public void run()
  {
    readSpace();
    // check if anything to read
    if (peek() == -1)
    {
      return;
    }
    // first statement must be the number of elevators
    String str = readString();
    if (!str.equals("elevators"))
    {
      error("expecting elevators, found '" + str + "'");
    }

    readSpace();

    elevatorCount = readInt();
    //System.out.println("elevatorCount: " + elevatorCount);

    if (elevatorCount < 1) {
      error("Invalid Elevator Number: " + elevatorCount);
    }

    elevatorFloors = new int[elevatorCount];

    for (int i = 0; i < elevatorCount; i++) elevatorFloors[i] = 1;

    readSpace();
    if(peek() == -1) return;

    // Second Statement must be the command for the elevators
    while (peek() != -1)
    {
      // Determines weather the elevator will go up or down
      readSpace();
      str = readString();
      //System.out.println("Str: " + str);
      if (!str.equals("up") & !str.equals("down")) {
        error("Expecting up or down, found: " + str);
      }

      // Which elevator is going to move
      readSpace();
      int cur = readInt();
      //System.out.println("Cur: " + cur);
      if (cur > elevatorCount | cur < 1) {
        error("Invalid Elevator Number: " + cur);
      }

      // How much the elevator is going to move
      readSpace();
      int floor_change = readInt();
      //System.out.println("Floor Change: " + floor_change);
      if (floor_change < 0) {
        error("invalid number of floors: " + floor_change);
      }
      if (str.equals("up") & (elevatorFloors[cur - 1] + floor_change) > 0) {
        elevatorFloors[cur - 1] = elevatorFloors[cur - 1] + floor_change;
      }
      else if (str.equals("up") & (elevatorFloors[cur - 1] + floor_change) < 0) {
        elevatorFloors[cur - 1] = elevatorFloors[cur - 1] + floor_change + 1;
      }
      else if (str.equals("down") & (elevatorFloors[cur - 1] - floor_change) < 0){
        elevatorFloors[cur - 1] = elevatorFloors[cur - 1] - floor_change - 1;
      }
      else {
        elevatorFloors[cur - 1] = elevatorFloors[cur - 1] - floor_change;
      }

      //for (int j = 0; j < elevatorCount; j++) System.out.println(elevatorFloors[j]);

      // Breaks loop if there is no more data left in the file
      readSpace();
      if(peek() == -1) return;
    }
  }

  /** 
   * Return the number of elevators resulting after running the
   * simulation.
   */
  public int elevators() {
    return elevatorCount;
  }

  /**
   * Return the floor the given elevator is on after running the
   * simulation.
   */
  public int floor(int elevator_num) {
    int floor_num = (int)Array.get(elevatorFloors, elevator_num - 1);
    return floor_num;
  }

}
