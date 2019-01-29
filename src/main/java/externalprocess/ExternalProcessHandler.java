package externalprocess;

import java.io.IOException;
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

    try {
      URL url = new URL("http://" + this.hostName + ":" + this.port + "/processmanagement/");
      this.conn = (HttpURLConnection) url.openConnection();

    } catch (MalformedURLException e) {

      e.printStackTrace();
      return false;

    } catch (IOException e) {

      e.printStackTrace();
      return false;
    }
    return true;
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
      // Runtime runtime = Runtime.getRuntime();
      this.process = new ProcessBuilder(path, String.valueOf(this.port)).start();
      // ArrayList<String> server = new ArrayList();
      // server.add("path");
      // server.add("5200");
      // this.process = runtime.exec(path, "5200");
      /*
       * InputStream processInputStream = process.getInputStream(); BufferedReader reader = new BufferedReader(new
       * InputStreamReader(processInputStream)); String line; while ((line = reader.readLine()) != null) {
       * System.out.println(line); }
       */
      /*
       * if (process.exitValue() == 0) { System.out.println("Salida correcta"); execution = true; } else {
       * System.out.println("Salida falsa"); execution = false; }
       */
      execution = true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      execution = false;
    }
    return execution;
  }

}