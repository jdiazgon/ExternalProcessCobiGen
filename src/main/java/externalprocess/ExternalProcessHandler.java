package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import externalprocess.utils.OutputHandler;

public class ExternalProcessHandler {

  private Integer port;

  private String hostName;

  private HttpURLConnection conn;

  private Process process;

  private OutputHandler err = null;

  private URL url;

  private String exePath = "";

  public static ExternalProcessHandler externalProcessHandler = null;

  public static ExternalProcessHandler getExternalProcessHandler(String hostName, Integer port) {

    if (externalProcessHandler == null) {
      externalProcessHandler = new ExternalProcessHandler(hostName, port);
    }

    return externalProcessHandler;
  }

  protected ExternalProcessHandler(String hostName, int port) {

    this.hostName = hostName;
    this.port = port;
  }

  public boolean ExecutinExe(String path) {

    this.exePath = path;

    boolean execution = false;
    try {
      System.out.println("Loading server: " + this.exePath);
      this.process = new ProcessBuilder(this.exePath, String.valueOf(this.port)).start();

      // We try to get the error output
      this.err = new OutputHandler(this.process.getErrorStream(), "UTF-8");

      while (!this.process.isAlive()) {
        System.out.println("Waiting process is alive");
      }
      execution = true;
    } catch (IOException e) {
      e.printStackTrace();
      execution = false;
    }
    return execution;

  }

  public boolean InitializeConnection() {

    boolean isConnected = false;
    int retry = 0;
    while (!isConnected && retry < 10) {
      try {
        startConnection();

        // Just check correct port acquisition
        while (acquirePort() == false)
          if (retry <= 5) {
            retry++;
            startConnection();
            continue;
          } else {
            return false;
          }

      } catch (ConnectException e) {
        retry++;
        System.out.println("Connection to server failed, attempt number " + retry + ".");
        System.out.println(e);
        try {
          Thread.sleep(100);
        } catch (InterruptedException eS) {
          eS.printStackTrace();
        }
      } catch (MalformedURLException e) {
        System.out.println("Connection to server failed, MalformedURL.");
        e.printStackTrace();
        return false;

      } catch (IOException e) {
        retry++;
        System.out.println("Connection to server failed, attempt number " + retry + ".");
        System.out.println(e);
        isConnected = false;
        try {
          Thread.sleep(100);
        } catch (InterruptedException eS) {
          eS.printStackTrace();
        }
      } finally {
        this.conn.disconnect();
        isConnected = true;
      }
    }
    return isConnected;
  }

  /**
   * @throws MalformedURLException
   * @throws IOException
   */
  private void startConnection() throws MalformedURLException, IOException {

    this.url = new URL("http://" + this.hostName + ":" + this.port + "/processmanagement/");
    this.conn = (HttpURLConnection) this.url.openConnection();
    this.conn.connect();
  }

  public boolean acquirePort() {

    // If there is any error, probably it is because the port is blocked
    if (processHasErrors() || isNotConnected()) {
      closeConnection();
      this.port++;
      ExecutinExe(this.exePath);
    } else {
      return true;
    }
    return false;
  }

  public boolean processHasErrors() {

    if (this.err != null) {
      // External process has not printed any error
      if (this.err.getText().isEmpty()) {
        return false;
      }
    }

    return true;
  }

  public boolean isNotConnected() {

    try {
      getConnection("HEAD", "Content-Type", "text/plain");
      int responseCode;
      responseCode = this.conn.getResponseCode();
      if (responseCode < 500) {
        return false;
      }
    } catch (IOException e) {
      System.out.println("Connection to server failed, port blocked. Trying other port...");
      System.out.println(e);
    }

    return true;
  }

  public HttpURLConnection getConnection(String httpMethod, String headerType, String mediaType) {

    try {
      this.conn = (HttpURLConnection) this.url.openConnection();

      this.conn.setDoOutput(true);
      this.conn.setRequestMethod(httpMethod);
      this.conn.setRequestProperty(headerType, mediaType);

    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this.conn;
  }

  public void closeConnection() {

    if (this.conn != null) {
      this.conn.disconnect();
    }
    if (this.process.isAlive()) {
      this.process.destroy();
      System.out.println("Closing process");
    }
  }

}
