package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import externalprocess.ConnectionExceptionHandler.ConnectionException;
import externalprocess.utils.OutputHandler;

/**
 * Class for handling the external process creation and the communication with it.
 *
 */
public class ExternalProcessHandler {

  private Integer port;

  private String hostName;

  private HttpURLConnection conn;

  private Process process;

  private OutputHandler errorHandler = null;

  private URL url;

  private String exePath = "";

  /**
   * Singleton instance of {@link ExternalProcessHandler}.
   */
  public static ExternalProcessHandler externalProcessHandler = null;

  /**
   * Using singleton pattern, we will only have one instance of {@link ExternalProcessHandler}.
   *
   * @param hostName name of the server, normally localhost
   * @param port port to be used for connecting to the server
   * @return
   */
  public static ExternalProcessHandler getExternalProcessHandler(String hostName, Integer port) {

    if (externalProcessHandler == null) {
      externalProcessHandler = new ExternalProcessHandler(hostName, port);
    }

    return externalProcessHandler;
  }

  /**
   * Constructor of {@link ExternalProcessHandler}.
   *
   * @param hostName name of the server, normally localhost
   * @param port port to be used for connecting to the server
   */
  protected ExternalProcessHandler(String hostName, int port) {

    this.hostName = hostName;
    this.port = port;
  }

  /**
   * Tries to execute, as a new process, the executable file specified on the parameter. Also initializes the error
   * handler.
   *
   * @param path of the executable file to execute
   * @return true if it was able to execute the exe successfully
   */
  public boolean executingExe(String path) {

    this.exePath = path;

    boolean execution = false;
    try {
      System.out.println("Loading server: " + this.exePath);
      this.process = new ProcessBuilder(this.exePath, String.valueOf(this.port)).start();

      // We try to get the error output
      this.errorHandler = new OutputHandler(this.process.getErrorStream(), "UTF-8");

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

  /**
   * Tries several times to start a new connection to the server. If the port is already in use, tries to connect again
   *
   * @return true if the ExternalProcess was able to connect to the server
   */
  public boolean InitializeConnection() {

    ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();
    connectionExc.setMalformedURLExceptionMessage("Connection to server failed, MalformedURL.");

    boolean isConnected = false;
    int retry = 0;
    while (!isConnected && retry < 10) {
      try {
        connectionExc.setConnectExceptionMessage("Connection to server failed, attempt number " + (retry + 1) + ".");
        connectionExc.setIOExceptionMessage("Connection to server failed, attempt number " + (retry + 1) + ".");
        retry++;

        startConnection();

        // Just check correct port acquisition
        while (!acquirePort())
          if (retry <= 5) {
            retry++;
            startConnection();
          } else {
            return false;
          }

      } catch (Exception e) {
          if(connectionExc.handle(e).equals(ConnectionException.MALFORMED_URL)) {
            return false;
          }

      } finally {
        this.conn.disconnect();
        isConnected = true;
      }
    }
    return isConnected;
  }

  /**
   * Tries to start a new connection to the server
   *
   * @throws MalformedURLException
   * @throws IOException
   */
  private void startConnection() throws IOException {

    this.url = new URL("http://" + this.hostName + ":" + this.port + "/processmanagement/");
    this.conn = (HttpURLConnection) this.url.openConnection();
    this.conn.connect();
  }

  /**
   * Tries to acquire a port. If the port is already in use, tries to execute again the external process with another
   * port
   *
   * @return true if a port has been acquired
   */
  public boolean acquirePort() {

    // If there is any error, probably it is because the port is blocked
    // dev processHasErrors() ||
    if (isNotConnected()) {
      closeConnection();
      this.port++;
      executingExe(this.exePath);
    } else {
      return true;
    }
    return false;
  }

  /**
   * Checks whether the external process error handler has any content. If so, it means there is an error
   *
   * @return true if external process contains errors
   */
  public boolean processHasErrors() {

    if (this.errorHandler != null) {
      // External process has not printed any error
      if (this.errorHandler.getText().isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Sends a dummy request to the server in order to check if it is not connected
   *
   * @return true if it is not connected
   */
  public boolean isNotConnected() {

    try {
      getConnection("HEAD", "Content-Type", "text/plain", "");
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

  /**
   * Gets an HTTP request using the specified parameters
   *
   * @param httpMethod HTTP method to use (POST, GET, PUT...)
   * @param headerProperty Header property to use (content-type, content-lenght
   * @param mediaType type of media (application/json, text/plain...)
   * @param endpointURL The endpoint URL of the service
   * @return the {@link HttpURLConnection} to the endpoint
   */
  public HttpURLConnection getConnection(String httpMethod, String headerProperty, String mediaType,
      String endpointURL) {
    try {
      URL currentURL = new URL(this.url.getProtocol(), this.url.getHost(), this.url.getPort(),
          this.url.getFile() + endpointURL);
      this.conn = (HttpURLConnection) currentURL.openConnection();

      this.conn.setDoOutput(true);
      this.conn.setRequestMethod(httpMethod);
      this.conn.setRequestProperty(headerProperty, mediaType);

    } catch (Exception e) {
      ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();
      connectionExc.handle(e);
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
    // Let's kill the process error handler
    if (this.errorHandler.isAlive()) {
      try {
        this.errorHandler.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
