package com.acme.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.refactor.settings.ProparseSettings;
import org.prorefactor.treeparser.ParseUnit;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.openedge.api.Constants;
import org.sonar.plugins.openedge.api.objects.DatabaseWrapper;
import org.testng.annotations.BeforeMethod;

import eu.rssw.antlr.database.objects.DatabaseDescription;
import eu.rssw.pct.RCodeInfo;
import eu.rssw.pct.RCodeInfo.InvalidRCodeException;

public abstract class AbstractTest {
  private static final String BASEDIR = "src/test/resources/";

  protected SensorContextTester context;
  protected RuleKey ruleKey;

  private RefactorSession session;
  private Schema schema;

  // FIXME Should be BeforeTest
  @BeforeMethod
  public void initContext() throws IOException {
    ruleKey = RuleKey.parse(Constants.RSSW_REPOSITORY_KEY + ":" + this.getClass().getName());

    try (FileInputStream input = new FileInputStream("src/test/resources/sp2k.schema")) {
      schema = new Schema(new DatabaseWrapper(DatabaseDescription.deserialize(input, "sp2k")));
      session = new RefactorSession(new ProparseSettings("src/test/resources"), schema);
    }

    // Inject all rcodes
    Files.walk(new File("src/test/resources").toPath(), FileVisitOption.FOLLOW_LINKS) //
      .filter(it -> Files.isRegularFile(it) && it.getFileName().toString().endsWith(".r")) //
      .forEach(it -> {
        try (InputStream is = Files.newInputStream(it)) {
          RCodeInfo rci = new RCodeInfo(is);
          session.injectTypeInfo(rci.getTypeInfo());
        } catch (IOException | InvalidRCodeException uncaught) {
          // No-op
        }
      });
  }

  @BeforeMethod
  public void initTest() throws IOException {
    context = SensorContextTester.create(new File(BASEDIR));

    // Common include files have to be referenced in the FileSystem object
    for (File f : new File(BASEDIR + "inc").listFiles()) {
      context.fileSystem().add(
          TestInputFileBuilder.create(BASEDIR, "inc/" + f.getName()).setLanguage(Constants.LANGUAGE_KEY).setType(
              Type.MAIN).setCharset(Charset.defaultCharset()).setContents(
                  Files.readString(new File(BASEDIR, "inc/" + f.getName()).toPath(),
                      Charset.defaultCharset())).build());
    }
  }

  public InputFile getInputFile(String file) {
    try {
      InputFile inputFile = TestInputFileBuilder.create(BASEDIR, file).setLanguage(Constants.LANGUAGE_KEY).setType(
          Type.MAIN).setCharset(Charset.defaultCharset()).setContents(
              Files.readString(new File(BASEDIR, file).toPath(), Charset.defaultCharset())).build();
      context.fileSystem().add(inputFile);

      return inputFile;
    } catch (IOException caught) {
      throw new RuntimeException(caught);
    }
  }

  public ParseUnit getParseUnit(InputFile file) {
    ParseUnit unit = null;
    try {
      unit = new ParseUnit(file.inputStream(), file.filename(), session);
      unit.treeParser01();
      unit.attachTypeInfo(session.getTypeInfo(unit.getRootScope().getClassName()));
    } catch (IOException caught) {
      throw new RuntimeException("Unable to parse file", caught);
    }
    return unit;
  }
}
