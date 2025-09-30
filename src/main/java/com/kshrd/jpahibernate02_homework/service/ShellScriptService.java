package com.kshrd.jpahibernate02_homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class ShellScriptService {

    @Value("${script.base.path:/scripts}")
    private String scriptBasePath;

    public String executeShellScript(String scriptName) {
        StringBuilder output = new StringBuilder();
        try {
            // Validate script name to prevent path traversal
            if (!scriptName.matches("[a-zA-Z0-9_-]+\\.sh")) {
                throw new IllegalArgumentException("Invalid script name");
            }
            String scriptPath = scriptBasePath + "/" + scriptName;
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            output.append("Exit Code: ").append(exitCode);

        } catch (Exception e) {
            output.append("Error executing script: ").append(e.getMessage());
        }
        return output.toString();
    }
}