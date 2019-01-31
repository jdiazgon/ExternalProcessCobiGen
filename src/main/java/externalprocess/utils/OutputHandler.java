package externalprocess.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import externalprocess.ExternalProcessHandler;

public class OutputHandler extends Thread {
  private final StringBuilder buf = new StringBuilder();

  private final BufferedReader in;

  public OutputHandler(InputStream in, String encoding) throws UnsupportedEncodingException {

    this.in = new BufferedReader(new InputStreamReader(in, encoding == null ? "UTF-8" : encoding));
    setDaemon(true);
    start();
  }

  public String getText() {

    synchronized (this.buf) {
      return this.buf.toString();
    }
  }

  @Override
  public void run() {

    // Reading process output
    try {
      String s = this.in.readLine();
      while (s != null) {
        synchronized (this.buf) {
          this.buf.append(s);
          this.buf.append('\n');
        }
        s = this.in.readLine();
      }
    } catch (IOException ex) {
      Logger.getLogger(ExternalProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        this.in.close();
      } catch (IOException ex) {
        Logger.getLogger(ExternalProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}