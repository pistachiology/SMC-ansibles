package org.custom.code;

import org.custom.helper.PreprocessGenerator;
import org.custom.helper.PreprocessGenerator.PreprocessDataType;


class GeneratePreprocessedInputDemo {
    public static void main(String args[]) {
        PreprocessGenerator generator = new PreprocessGenerator(1, 3, 1);
        generator.input(1000, PreprocessDataType.SInt);
        generator.input(1000, PreprocessDataType.SInt);
        generator.input(3000, PreprocessDataType.OInt);
        generator.exportToFile();
        generator = new PreprocessGenerator(2, 3, 1);
        generator.input(10000, PreprocessDataType.SInt);
        generator.exportToFile();
    }
}