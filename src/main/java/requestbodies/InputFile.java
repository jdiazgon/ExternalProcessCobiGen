package requestbodies;

import java.io.File;

public class InputFile {
  String path;

  public InputFile(String filePath) {

    this.path = filePath;
  }

  /**
   * @return filePath
   */
  public String getPath() {

    String escapedSeparator = File.separator + File.separator;

    return this.path.replaceAll(escapedSeparator, escapedSeparator + escapedSeparator);
  }

  /**
   * @param filePath new value of {@link #getfilePath}.
   */
  public void setPath(String filePath) {

    this.path = filePath;
  }

  public String toJSON() {

    return "{\"path\":\"" + this.path + "\"}";
  }

}
