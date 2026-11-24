package com.farm.programming;

import com.farm.entity.FarmerDrone;

import java.util.ArrayList;
import java.util.List;

// IDEA: Batch running (e.g. one Runner instance, List<FarmerDrone>)
public class Runner {
  private final FarmerDrone drone;
  private String source;
  private int lineCount;
  private List<String> lines = new ArrayList<>();
  private boolean isRunning = false;
  private boolean isLoop = false;

  public Runner(FarmerDrone drone, String source) {
    this.drone = drone;
    this.setSource(source);
  }

  public int execLine(int line) {
    try {
      String command = lines.get(line);
      String[] args = command.split("\\s+");
      int statusCode;

      switch (args[0]) {
        case "MOV":
          statusCode = handleArgCountError(args, 5);
          if (statusCode != 0) return statusCode;
          statusCode = handleTypeError(new String[]{args[1], args[2], args[3]}, Float.class);
          if (statusCode != 0) return statusCode;
          statusCode = handleTypeError(new String[]{args[4]}, Integer.class);
          if (statusCode != 0) return statusCode;
          this.drone.move(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), Integer.parseInt(args[4]));
        case "ROT":
          statusCode = handleArgCountError(args, 2);
          if (statusCode != 0) return statusCode;
          statusCode = handleTypeError(new String[]{args[1]}, Integer.class);
          if (statusCode != 0) return statusCode;
          if (Integer.parseInt(args[1]) % 11.25f != 0) return ResponseCode.UNKNOWN_ERROR.getValue();
          this.drone.rotate(Integer.parseInt(args[1]));
        default:
          return ResponseCode.UNKNOWN_ERROR.getValue();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private int handleArgCountError(String[] args, int expected) {
    int length = args.length;
    if (length < expected) return ResponseCode.ARG_NOT_FOUND_ERROR.getValue();
    if (length > expected) return ResponseCode.REDUNDANT_ARG_ERROR.getValue();
    return 0;
  }

  private int handleTypeError(String[] args, Object expected) {
    if (expected instanceof Integer) {
      try {
        for (String arg: args) Integer.parseInt(arg);
      } catch (NumberFormatException e) {
        return ResponseCode.INVALID_TYPE_ERROR.getValue();
      }
    }
    if (expected instanceof Float) {
      try {
        for (String arg: args) Float.parseFloat(arg);
      } catch (NumberFormatException e) {
        return ResponseCode.INVALID_TYPE_ERROR.getValue();
      }
    }
    return 0;
  }

  public boolean isLoop() {
    return this.isLoop;
  }

  public void setLoop(boolean isLoop) {
    this.isLoop = isLoop;
  }

  public int getLineCount() {
    return this.lineCount;
  }

  public String getSource() {
    return this.source;
  }

  public void setSource(String source) {
    this.source = source;
    this.lines = source.lines().toList();
    this.lineCount = lines.size();
  }

  public boolean isRunning() {
    return this.isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }
}
