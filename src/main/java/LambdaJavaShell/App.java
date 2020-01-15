package LambdaJavaShell;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Map;

public class App implements RequestHandler<Map<String,Object>, String> {
    private Context context = null;

    public static void main(String[] args) {
        String decoded = new String(Base64.getDecoder().decode(args[0].getBytes()));
        System.out.println(new App().runCmd(decoded));
    }

    @Override
    public String handleRequest(Map<String,Object> input, Context context) {
        String cmd = input.get("command").toString();
        String decoded = new String(Base64.getDecoder().decode(cmd.getBytes()));
        return runCmd(decoded);
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
            return Base64.getEncoder().encodeToString(output.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}