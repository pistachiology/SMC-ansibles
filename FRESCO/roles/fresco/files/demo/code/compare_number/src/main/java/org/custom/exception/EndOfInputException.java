package org.custom.exception;

public class EndOfInputException extends Exception
{
      // Parameterless Constructor
      public EndOfInputException() {}

      // Constructor that accepts a message
      public EndOfInputException(String message)
      {
         super(message);
      }
 }