package com.acme.rules;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.batch.fs.internal.FileMetadata.Metadata;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.plugins.openedge.api.Constants;
import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.RefactorException;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.refactor.settings.ProparseSettings;
import org.prorefactor.treeparser.ParseUnit;
import org.testng.annotations.BeforeMethod;

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
  public void initTest() {
    context = SensorContextTester.create(new File(BASEDIR));

    // Common include files have to be referenced in the FileSystem object
    for (File f : new File(BASEDIR + "inc").listFiles()) {
      Metadata metadata = new FileMetadata().readMetadata(f, Charset.defaultCharset());
      DefaultInputFile inputFile = new DefaultInputFile("test", BASEDIR + "inc/" + f.getName()).setLanguage(
          Constants.LANGUAGE_KEY).initMetadata(metadata);
      context.fileSystem().add(inputFile);
    }
  }

  public InputFile getInputFile(String file) {
    Metadata metadata = new FileMetadata().readMetadata(new File(BASEDIR + file), Charset.defaultCharset());
    DefaultInputFile inputFile = new DefaultInputFile("test", file).setLanguage("oe").initMetadata(metadata);
    context.fileSystem().add(inputFile);

    return inputFile;
  }

  public ParseUnit getParseUnit(InputFile file) {
    ParseUnit unit = new ParseUnit(file.file(), session);
    try {
      unit.treeParser01();
    } catch (RefactorException caught) {
      throw new RuntimeException("Unable to parse file", caught);
    }
    return unit;
  }
}
