package externalprocess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class ConnectionExceptionHandler {

	private String connectExceptionMessage;

	private String malformedURLExceptionMessage;

  private String IOExceptionMessage;

  private String protocolExceptionMessage;

  public ConnectionExceptionHandler() {

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
		return IOExceptionMessage;
	}

	public ConnectionExceptionHandler setIOExceptionMessage(String iOExceptionMessage) {
		IOExceptionMessage = iOExceptionMessage;

		return this;
	}

	public String getProtocolExceptionMessage() {
		return protocolExceptionMessage;
	}

	public ConnectionExceptionHandler setProtocolExceptionMessage(String protocolExceptionMessage) {
		this.protocolExceptionMessage = protocolExceptionMessage;

		return this;
	}


	public void handle(ConnectException e) {
		System.out.println(this.connectExceptionMessage);

    try {
      Thread.sleep(100);
    } catch (InterruptedException eS) {
      eS.printStackTrace();
    }
	}

	public void handle(MalformedURLException e) {
	  System.out.println(this.malformedURLExceptionMessage);
	  e.printStackTrace();
	}

	public void handle(IOException e) {
	  try {
      Thread.sleep(100);
    } catch (InterruptedException eS) {
      eS.printStackTrace();
    }
	}

	public void handle(ProtocolException e) {

	}
}
