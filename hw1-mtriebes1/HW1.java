
/**
 * Author: Matthew Triebes
 * Course: CPSC 326, Spring 2020
 * Asgnmt: HW 1 - HW1.java
 * 
 * Description: Simple program for practicing reading from a file,
 * parsing its contents, and outputing the results.
 */

import java.io.*;


public class HW1 {

  /**
   * Runs the elevator simulation
   */ 
  public static void main(String[] args) {
    // ensure file argument given
    if (args.length != 1) {
      System.out.println("Usage: java HW1 infile");
      System.exit(1);
    }
    try {
      // create the simulation
      InputStream instream = new FileInputStream(new File(args[0]));
      ElevatorSimulator sim = new ElevatorSimulator(instream);

      // Shows the test number
      System.out.println(" ");
      System.out.println(args[0] + " output");

      // run the simulation
      sim.run();

      // print the results
      for (int i = 1; i <= sim.elevators(); ++i)
      {
        int floor = sim.floor(i);
        System.out.println("Elevator " + i + " is on floor " + floor);
      }
      System.out.println(" ");

    } catch(FileNotFoundException e) {
      System.out.println("Unable to open file '" + args[0] + "'");
      System.exit(1);
    }
  }

}
