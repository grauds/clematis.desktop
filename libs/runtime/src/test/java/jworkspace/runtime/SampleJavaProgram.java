package jworkspace.runtime;

import java.util.logging.Level;

import lombok.extern.java.Log;

@Log
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class SampleJavaProgram {

    public static void main(String[] args) {
        log.log(Level.INFO, String.valueOf(1));
    }
}
