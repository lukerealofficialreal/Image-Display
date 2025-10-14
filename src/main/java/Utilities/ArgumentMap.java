package Utilities;

import exceptions.InvalidArgumentException;
import exceptions.InvalidArgumentValueException;

import java.util.HashMap;

public class ArgumentMap {
    private HashMap<String, Object> argsMap = new HashMap<>();

    //Takes the actual arguments,
    //Some expected arguments, and their parameter types
    public ArgumentMap(String[] args, String[] expectedArgs, Object[] defaultValues) throws InvalidArgumentException {
        //Fill the argsMap with default values
        for(int i = 0; i < expectedArgs.length; i++) {
            argsMap.put(expectedArgs[i], defaultValues[i]);
        }

        //For each provided argument:
        //- check if it is a valid argument
        //- check the type of the corresponding default value
        //- if it is a flag, i-- as there is no provided value, the value is implicitly True
        //  else, i+1 is the argument's value
        //- Add the argument and the value to the hashmap
        //- if the argument is not valid or the value is not valid, throw an exception
        for(int i = 0; i < args.length; i+=2) {
            int validLoc = validateArg(args[i], expectedArgs);
            if(validLoc < 0) {
                throw new InvalidArgumentException(args[i]);
            }

            try {
                if (defaultValues[validLoc] instanceof Boolean) {
                    argsMap.put(expectedArgs[validLoc], Boolean.TRUE);
                    i--;
                } else {
                    argsMap.put(expectedArgs[validLoc], fromString(defaultValues[validLoc], args[i + 1]));
                }
            } catch (InvalidArgumentValueException e) {
                throw new InvalidArgumentException(args[i], e.getMessage());
            }
        }
    }

    //if the argument is a valid argument, returns the index of the argument in expectedArgs.
    //Else, returns -1
    public int validateArg(String arg, String[] expectedArgs) {
        for(int i = 0; i < expectedArgs.length; i++) {
            if(arg.equals(expectedArgs[i])) {
                return i;
            }
        }
        return -1;
    }

    public Object getVal(String arg) {
        return argsMap.get(arg);
    }

    public static Object fromString(Object example, String value) throws InvalidArgumentValueException {
        try {
            return switch (example) {
                case String s -> value;
                case Integer i -> Integer.valueOf(value);
                case Double d -> Double.valueOf(value);
                case Long l -> Long.valueOf(value);
                case Float f -> Float.valueOf(value);
                case Short sh -> Short.valueOf(value);
                case Byte b -> Byte.valueOf(value);
                default -> throw new InvalidArgumentValueException(value); //Should have a different exception probably
            };
        } catch(NumberFormatException e) {
            throw new InvalidArgumentValueException(value);
        }
    }
}
