package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ExternalProcessHandler {

  private Integer port;

  private String hostName;

  private HttpURLConnection conn;

  private Process process;

  private URL url;

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

    boolean execution;
    try {
      System.out.println("Loading server: " + path);
      this.process = new ProcessBuilder(path, String.valueOf(this.port)).start();
      if (!this.process.isAlive()) {
        while (!this.process.isAlive()) {
          System.out.println("Waiting process is alive");
        }
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
        this.url = new URL("http://" + this.hostName + ":" + this.port + "/processmanagement/");
        this.conn = (HttpURLConnection) this.url.openConnection();
        this.conn.connect();
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

    this.conn.disconnect();
    if (this.process.isAlive()) {
      this.process.destroy();
      System.out.println("Closing server");
    }
  }

}
