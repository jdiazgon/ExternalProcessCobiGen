package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.time.chrono.IsoChronology;
import java.util.LinkedList;
import java.util.List;

public class ConnectionExceptionHandler {

	private String connectExceptionMessage;

	private String malformedURLExceptionMessage;

  private String ioExceptionMessage;

  private String protocolExceptionMessage;

  public ConnectionExceptionHandler() {
    this.connectExceptionMessage = "";
    this.malformedURLExceptionMessage = "";
    this.ioExceptionMessage = "";
    this.protocolExceptionMessage = "";

  }

	public String getConnectExceptionMessage() {
		return connectExceptionMessage;
	}

	public ConnectionExceptionHandler setConnectExceptionMessage(String connectExceptionMessage) {
		this.connectExceptionMessage = connectExceptionMessage;

		return this;
	}

	public String getMalformedURLExceptionMessage() {
		return malformedURLExceptionMessage;
	}

	public ConnectionExceptionHandler setMalformedURLExceptionMessage(String malformedURLExceptionMessage) {
		this.malformedURLExceptionMessage = malformedURLExceptionMessage;

		return this;
	}

	public String getIOExceptionMessage() {
		return ioExceptionMessage;
	}

	public ConnectionExceptionHandler setIOExceptionMessage(String ioExceptionMessage) {
		this.ioExceptionMessage = ioExceptionMessage;

		return this;
	}

	public String getProtocolExceptionMessage() {
		return protocolExceptionMessage;
	}

	public ConnectionExceptionHandler setProtocolExceptionMessage(String protocolExceptionMessage) {
		this.protocolExceptionMessage = protocolExceptionMessage;

		return this;
	}


	public List<ConnectionException> handle(Exception e) {

	  List<ConnectionException> exceptions = new LinkedList<ConnectionException>();

	  boolean isConnectException = e instanceof ConnectException;
	  boolean isIOException = e instanceof IOException;

	  if (isConnectException) {
	    System.out.println(this.connectExceptionMessage);
	    exceptions.add(ConnectionException.CONNECT);
	  }

	  if (isIOException) {
      System.out.println(this.ioExceptionMessage);
      exceptions.add(ConnectionException.IO);
    }

	  if (isConnectException || isIOException) {
	    try {
	      Thread.sleep(100);
	    } catch (InterruptedException eS) {
	      eS.printStackTrace();
	    }
	  }

	  if (e instanceof MalformedURLException) {
	    System.out.println(this.malformedURLExceptionMessage);
	    e.printStackTrace();

	    exceptions.add(ConnectionException.MALFORMED_URL);
	  }

	  if (e instanceof ProtocolException) {

	    exceptions.add(ConnectionException.PROTOCOL);
	  }

    return exceptions;
	}

	public enum ConnectionException {
	  CONNECT,
	  MALFORMED_URL,
	  IO,
	  PROTOCOL,
	  EXCEPTION;
	}
}
