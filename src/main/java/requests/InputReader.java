package requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import externalprocess.ExternalProcessHandler;
import externalprocess.ProcessConstants;
import requestbodies.InputFile;

public class InputReader {
  ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(ProcessConstants.hostName,
      ProcessConstants.port);

  /**
   * @param inputFile
   * @return
   */
  public boolean isValidInput(InputFile inputFile) {

    HttpURLConnection conn = this.request.getConnection();
    try {
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");

      OutputStream os = conn.getOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
      osw.write(inputFile.toJSON());
      osw.flush();
      osw.close();
      os.close();
      conn.connect();

      if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

      String output;
      System.out.println("Output from Server .... \n");
      while ((output = br.readLine()) != null) {
        System.out.println(output);
        conn.disconnect();
        return output.equals("true");
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }
}
