package requestbodies;

/**
 * Contains all the properties needed for requesting a merge on two files. It gets serialized and sent over the network
 */
public class MergeTO {

  private String baseContent;

  private String patchContent;

  private Boolean patchOverrides;

  /**
   * The constructor.
   *
   * @param baseContent
   * @param patchContent
   * @param patchOverrides
   */
  public MergeTO(String baseContent, String patchContent, Boolean patchOverrides) {

    super();
    this.baseContent = baseContent;
    this.patchContent = patchContent;
    this.patchOverrides = patchOverrides;
  }

  /**
   * @return baseContent
   */
  public String getBaseContent() {

    return this.baseContent;
  }

  /**
   * @param baseContent new value of {@link #getbaseContent}.
   */
  public void setBaseContent(String baseContent) {

    this.baseContent = baseContent;
  }

  /**
   * @return patchContent
   */
  public String getPatchContent() {

    return this.patchContent;
  }

  /**
   * @param patchContent new value of {@link #getpatchContent}.
   */
  public void setPatchContent(String patchContent) {

    this.patchContent = patchContent;
  }

  /**
   * @return patchOverrides
   */
  public Boolean getPatchOverrides() {

    return this.patchOverrides;
  }

  /**
   * @param patchOverrides new value of {@link #getpatchOverrides}.
   */
  public void setPatchOverrides(Boolean patchOverrides) {

    this.patchOverrides = patchOverrides;
  }

}
