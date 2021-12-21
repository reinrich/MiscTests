package test.trywithresources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestSimple {

    public static void main(String[] args) {
        try {
            String line = readFirstLineFromFile(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readFirstLineFromFile(String path) throws IOException {
        try (BufferedReader br =
                       new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

}
