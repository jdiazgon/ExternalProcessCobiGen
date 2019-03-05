package requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
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

    HttpURLConnection conn = this.request.getConnection("POST", "Content-Type", "application/json", "isValidInput");
    try {
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
        return output.equals("true");
      }

    } catch (ConnectException e) {

      System.out.println("Connection to server failed, attempt number " + 0 + ".");
      e.printStackTrace();
    } catch (IOException e) {

      e.printStackTrace();
    } catch (IllegalStateException e) {

      System.out.println("Closing connection on InputReader.");
      System.out.println(e);
      this.request.closeConnection();
      e.printStackTrace();
    }
    return false;
  }
}
