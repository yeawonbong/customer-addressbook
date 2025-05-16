package com.hyundai.test.address.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader {

    public List<String> read(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        InputStreamReader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

}
