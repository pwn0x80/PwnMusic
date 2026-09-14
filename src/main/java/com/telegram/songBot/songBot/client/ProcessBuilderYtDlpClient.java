package com.telegram.songBot.songBot.client;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessBuilderYtDlpClient implements YtDlpClient {

    @Override
    public List<String> executeProcess(List<String> command) throws IOException, InterruptedException {
        List<String> lines = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line.trim());
                }
            }
        }
        process.waitFor();
        return lines;
    }
}
