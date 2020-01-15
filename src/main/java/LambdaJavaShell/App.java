package LambdaJavaShell;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class App implements RequestHandler<Map<String,Object>, String> {
    private Context context = null;

    public static void main(String[] args) {
        System.out.println("Call it from Lambda function");
    }

    @Override
    public String handleRequest(Map<String,Object> input, Context context) {
        return runCmd(input.get("command").toString());
    }

    public String runCmd(String cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("/bin/sh", "-c", cmd);

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            System.out.println("Exit status: " + exitVal);
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}