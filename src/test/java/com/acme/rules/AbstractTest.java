package com.acme.rules;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.refactor.settings.ProparseSettings;
import org.prorefactor.treeparser.ParseUnit;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.plugins.openedge.api.Constants;
import org.testng.annotations.BeforeMethod;

import com.google.common.io.Files;

import antlr.ANTLRException;

public abstract class AbstractTest {
  private static final String BASEDIR = "src/test/resources/";

  protected SensorContextTester context;
  private RefactorSession session;
  private Schema schema;

  @BeforeMethod
  public void initContext() throws IOException {
    schema = new Schema("src/test/resources/sp2k.schema");
    session = new RefactorSession(new ProparseSettings("src/test/resources"), schema);
  }

  @BeforeMethod
  public void initTest() throws IOException {
    context = SensorContextTester.create(new File(BASEDIR));

    // Common include files have to be referenced in the FileSystem object
    for (File f : new File(BASEDIR + "inc").listFiles()) {
      context.fileSystem().add(new TestInputFileBuilder(BASEDIR, "inc/" + f.getName()).setLanguage(
          Constants.LANGUAGE_KEY).setType(Type.MAIN).initMetadata(
              Files.asCharSource(new File(BASEDIR, "inc/" + f.getName()), Charset.defaultCharset()).read()).build());
    }
  }

  public InputFile getInputFile(String file) throws IOException {
    InputFile inputFile = new TestInputFileBuilder(BASEDIR, file).setLanguage(Constants.LANGUAGE_KEY).setType(
        Type.MAIN).initMetadata(Files.asCharSource(new File(BASEDIR, file), Charset.defaultCharset()).read()).build();
    context.fileSystem().add(inputFile);

    return inputFile;
  }

  public ParseUnit getParseUnit(InputFile file) {
    ParseUnit unit = new ParseUnit(file.file(), session);
    try {
      unit.treeParser01();
    } catch (ANTLRException caught) {
      throw new RuntimeException("Unable to parse file", caught);
    }
    return unit;
  }
}
