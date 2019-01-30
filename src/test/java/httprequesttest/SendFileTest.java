package httprequesttest;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import externalprocess.ExternalProcessHandler;
import externalprocess.ProcessConstants;
import requestbodies.InputFile;
import requests.InputReader;

public class SendFileTest {

  @Test
  public void checkFileSentCorrectly() {

    ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(ProcessConstants.hostName,
        ProcessConstants.port);
    InputReader inputReader = new InputReader();
    String filePath = "C:\\" + File.separator + "Users\\" + File.separator + "whatever.nest";
    InputFile inputFile = new InputFile(filePath);

    assertEquals(request.ExecutinExe(ProcessConstants.exePath), true);

    assertEquals(request.InitializeConnection(), true);
    assertEquals(inputReader.isValidInput(inputFile), true);
    assertEquals(inputReader.isValidInput(inputFile), true);
    request.closeConnection();
  }

}
