package requestbodies;

public class InputFile {
  String path;

  public InputFile(String filePath) {

    this.path = filePath;
  }

  /**
   * @return filePath
   */
  public String getPath() {

    return this.path;
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
