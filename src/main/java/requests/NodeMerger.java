package requests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.devonfw.cobigen.api.exception.MergeException;

import externalprocess.ExternalProcessHandler;
import externalprocess.ProcessConstants;
import requestbodies.MergeTO;

/**
 * Merger class
 *
 * @since 0.0.1
 */
public class NodeMerger {
  ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(ProcessConstants.hostName,
      ProcessConstants.port);

  /** OS specific line separator */
  private static final String LINE_SEP = System.getProperty("line.separator");

  /** Merger Type to be registered */
  private String type;

  /** The conflict resolving mode */
  private boolean patchOverrides;

  /**
   * Creates a new {@link TypeScriptMerger}
   *
   * @param type merger type
   * @param patchOverrides if <code>true</code>, conflicts will be resolved by using the patch contents<br>
   *        if <code>false</code>, conflicts will be resolved by using the base contents
   */
  public NodeMerger(String type, boolean patchOverrides) {

    this.type = type;
    this.patchOverrides = patchOverrides;
  }

  public String getType() {

    return this.type;
  }

  /**
   * @param inputFile
   * @return
   */
  public String merge(File base, String patch, String targetCharset) {

    String baseFileContents;
    try {
      baseFileContents = new String(Files.readAllBytes(base.toPath()), Charset.forName(targetCharset));
    } catch (IOException e) {
      throw new MergeException(base, "Could not read base file!", e);
    }

    MergeTO mergeTO = new MergeTO(baseFileContents, patch, this.patchOverrides);

    HttpURLConnection conn = this.request.getConnection("POST", "Content-Type", "application/json", "merge");
    // Used for sending serialized objects
    ObjectWriter objWriter;
    try {
      OutputStream os = conn.getOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

      objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String jsonMergerTO = objWriter.writeValueAsString(mergeTO);

      // We need to escape new lines because otherwise our JSON gets corrupted
      jsonMergerTO = jsonMergerTO.replace("\\n", "\\\\n");

      osw.write(jsonMergerTO);
      osw.flush();
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
        return output;
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
    return "Not able to merge";
  }
}
