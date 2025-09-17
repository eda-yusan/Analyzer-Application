package com.project.github.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputWriter {  

    public static void writeToFile(List<String> lines, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            //BufferedWriter ve FileWriter kullanarak dosyaya yazma işlemi başlatılır.
            for (String line : lines) {   //Döngü ile listedeki her satır tek tek dosyaya yazılır.
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Output written to " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to write output: " + e.getMessage());
        }
    }
}
