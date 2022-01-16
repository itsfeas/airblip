package com.example.airblip;

import org.python.util.PythonInterpreter;

public class PythonEnv {
    public static void run() {
        try(PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("print('Hello Python World!')");
        }
    }
}