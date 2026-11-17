package com.farm.programming;

import com.farm.entity.FarmerDrone;

import java.util.Scanner;

public class Runner {
  private final FarmerDrone drone;
  private final Scanner scanner;

  public Runner(FarmerDrone drone, String source) {
    this.drone = drone;
    this.scanner = new Scanner(source);
  }

  public int exec() {
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] args = line.split("\\s+");
      int statusCode;

      switch (args[0]) {
        case "MOV":
          statusCode = handleArgCountError(args, 3);
          if (statusCode != 0) return statusCode;
          statusCode = handleTypeError(new String[]{args[1], args[2]}, (int) 0);
          if (statusCode != 0) return statusCode;

          drone.move(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        case "ROT":
          statusCode = handleArgCountError(args, 2);
          if (statusCode != 0) return statusCode;
          statusCode = handleTypeError(new String[]{args[1]}, (int) 0);
          if (statusCode != 0) return statusCode;

          drone.rotate(Integer.parseInt(args[1]));
        default:
          return ErrorCode.UNKNOWN.getValue();
      }
    }
    return 0;
  }

  private int handleArgCountError(String[] args, int expected) {
    int length = args.length;
    if (length < expected) return ErrorCode.ARG_NOT_FOUND.getValue();
    if (length > expected) return ErrorCode.REDUNDANT_ARG.getValue();
    return 0;
  }

  private int handleTypeError(String[] args, Object expected) {
    if (expected instanceof Integer) {
      try {
        for (String arg: args) Integer.parseInt(arg);
      } catch (NumberFormatException e) {
        return ErrorCode.INVALID_TYPE.getValue();
      }
    }
    return 0;
  }
}
