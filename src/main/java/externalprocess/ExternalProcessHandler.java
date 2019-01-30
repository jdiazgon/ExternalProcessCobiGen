package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ExternalProcessHandler {

  private Integer port;

  private String hostName;

  private HttpURLConnection conn;

  private Process process;

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

  public boolean InitializeConnection() {

    boolean connection = false;
    int retry = 0;
    while (!connection && retry < 10) {
      try {
        URL url = new URL("http://" + this.hostName + ":" + this.port + "/processmanagement/");
        this.conn = (HttpURLConnection) url.openConnection();
        connection = true;
      } catch (ConnectException e) {
        e.printStackTrace();
        retry++;
        System.out.println("Connection to server failed, attempt number " + retry + ".");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException eS) {
          // TODO Auto-generated catch block
          eS.printStackTrace();
        }
      } catch (MalformedURLException e) {
        System.out.println("Connection to server failed, MalformedURL.");
        e.printStackTrace();
        return false;

      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Connection to server failed, attempt number " + retry + ".");
        connection = false;
        retry++;
        try {
          Thread.sleep(1000);
        } catch (InterruptedException eS) {
          // TODO Auto-generated catch block
          eS.printStackTrace();
        }
      }
    }
    return connection;
  }

  public HttpURLConnection getConnection() {

    return this.conn;
  }

  public void closeConnection() {

    this.conn.disconnect();
    if (this.process.isAlive()) {
      this.process.destroy();
      System.out.println("Closing server");
    }
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
      // TODO Auto-generated catch block
      e.printStackTrace();
      execution = false;
    }
    return execution;
  }

}
