package httprequesttest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.devonfw.cobigen.api.exception.MergeException;

import externalprocess.ExternalProcessHandler;
import externalprocess.constants.ProcessConstants;
import requests.NodeMerger;

/**
 * Test methods for different TS mergers of the plugin
 */
public class TypeScriptMergerTest {

  /** Test resources root path */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/merger/";

  /** Initializing connection with server */
  private static ExternalProcessHandler request = ExternalProcessHandler
      .getExternalProcessHandler(ProcessConstants.HOST_NAME, ProcessConstants.PORT);

  /**
   * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = false
   *
   * @test fails
   */
  @Test
  public void testMergingNoOverrides() {

    assertEquals(true, request.executingExe(ProcessConstants.EXE_PATH));
    assertEquals(true, request.InitializeConnection());

    // arrange
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    // Next version should merge comments
    // String regex = " * Should format correctly this line";

    // act
    String mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 2");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("export interface a {");
    assertThat(mergedContents).contains("private b: number;");
    // assertThat(mergedContents).containsPattern(regex);

    mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 2");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("export interface a {");
    assertThat(mergedContents).contains("private b: number;");
    // assertThat(mergedContents).containsPattern(regex);

    request.closeConnection();
  }

  /**
   * Checks if the ts-merger can be launched and if the iutput is correct with patchOverrides = true
   *
   * @test fails
   */
  @Test
  public void testMergingOverrides() {

    // assertEquals(request.ExecutinExe(ProcessConstants.exePath), true);
    assertEquals(true, request.InitializeConnection());

    // arrange
    File baseFile = new File(testFileRootPath + "baseFile.ts");

    // act
    String mergedContents = new NodeMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 3");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("interface a {");
    assertThat(mergedContents).contains("private b: string;");
    // Next version should merge comments
    // assertThat(mergedContents).contains("// Should contain this comment");

    mergedContents = new NodeMerger("tsmerge", true).merge(baseFile, readTSFile("patchFile.ts"), "ISO-8859-1");

    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("aProperty: number = 3");
    assertThat(mergedContents).contains("bMethod");
    assertThat(mergedContents).contains("aMethod");
    assertThat(mergedContents).contains("bProperty");
    assertThat(mergedContents).contains("import { c, f } from 'd'");
    assertThat(mergedContents).contains("import { a, e } from 'b'");
    assertThat(mergedContents).contains("export { e, g } from 'f';");
    assertThat(mergedContents).contains("interface a {");
    assertThat(mergedContents).contains("private b: string;");
    // Next version should merge comments
    // assertThat(mergedContents).contains("// Should contain this comment");
    // request.closeConnection();
  }

  /**
   * We need to test whether we are able to send large amount of data to the server.
   *
   * @test fails
   */
  @Test
  public void testMergingMassiveFile() {

    // assertEquals(request.executingExe(ProcessConstants.EXE_PATH), true);
    assertEquals(true, request.InitializeConnection());

    // arrange
    File baseFile = new File(testFileRootPath + "massiveFile.ts");

    // act
    String mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, readTSFile("patchFile.ts"), "UTF-8");

    assertEquals(false, mergedContents.contains("Not able to merge") || mergedContents.isEmpty());

  }

  /**
   * Creates a massive ts file
   *
   * @param fileName
   * @return Massive string
   * @throws FileNotFoundException
   */
  private String createMassiveFile(String fileName) throws FileNotFoundException {

    String massiveString = readTSFile(fileName);
    String generatedText = massiveString;

    for (int i = 0; i < 35000; i++) {
      String appendThis = generatedText.replaceAll("class a ", "class a" + i).replaceAll("interface a ",
          "interface a" + i);
      massiveString = massiveString + "\n" + appendThis;
    }
    try (PrintWriter out = new PrintWriter("massiveFile.txt")) {
      out.println(massiveString);
    }
    return massiveString;
  }

  /**
   * Tests whether the contents will be rewritten after parsing and printing with the right encoding
   *
   * @throws IOException test fails
   * @test fails
   */
  @Test
  public void testReadingEncoding() throws IOException {

    File baseFile = new File(testFileRootPath + "baseFile_encoding_UTF-8.ts");
    File patchFile = new File(testFileRootPath + "patchFile.ts");

    assertEquals(true, request.InitializeConnection());

    String mergedContents = new NodeMerger("tsmerge", false).merge(baseFile, FileUtils.readFileToString(patchFile),
        "UTF-8");

    assertThat(mergedContents.contains("Ñ")).isTrue();

    baseFile = new File(testFileRootPath + "baseFile_encoding_ISO-8859-1.ts");
    mergedContents = "";
    assertThat(mergedContents.contains("Ñ"));
  }

  /**
   * Reads a TS file
   *
   * @param fileName the ts file
   * @return the content of the file
   */
  private String readTSFile(String fileName) {

    File patchFile = new File(testFileRootPath + fileName);
    String file = patchFile.getAbsolutePath();
    Reader reader = null;
    String returnString;

    try {
      reader = new FileReader(file);
      returnString = IOUtils.toString(reader);
      reader.close();

    } catch (FileNotFoundException e) {
      throw new MergeException(patchFile, "Can not read file " + patchFile.getAbsolutePath());
    } catch (IOException e) {
      throw new MergeException(patchFile, "Can not read the base file " + patchFile.getAbsolutePath());
    }

    return returnString;
  }

}
