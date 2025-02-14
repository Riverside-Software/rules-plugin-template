package com.acme.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

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
  private static final Path BASEDIR = Path.of("src/test/resources/");

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
    context = SensorContextTester.create(BASEDIR.toAbsolutePath());

    // Common include files have to be referenced in the FileSystem object
    Files.walk(BASEDIR.resolve("inc")).filter(Files::isRegularFile) //
      .filter(it -> it.getFileName().toString().endsWith(".i")) //
      .forEach(it -> {
        try {
          String content = Files.readString(it, StandardCharsets.UTF_8);
          InputFile inputFile = TestInputFileBuilder.create("module", BASEDIR.toFile(), it.toFile()) //
            .setLanguage(Constants.LANGUAGE_KEY) //
            .setType(Type.MAIN) //
            .setCharset(StandardCharsets.UTF_8) //
            .setContents(content) //
            .build();
          context.fileSystem().add(inputFile);
        } catch (IOException caught) {
          throw new UncheckedIOException(caught);
        }
      });
  }

  public InputFile getInputFile(String file) {
    try {
      String content = Files.readString(BASEDIR.resolve(file), StandardCharsets.UTF_8);
      InputFile inputFile = TestInputFileBuilder.create("module", BASEDIR.toFile(), BASEDIR.resolve(file).toFile()) //
        .setLanguage(Constants.LANGUAGE_KEY) //
        .setType(Type.MAIN) //
        .setCharset(StandardCharsets.UTF_8) //
        .setContents(content) //
        .build();
      context.fileSystem().add(inputFile);

      return inputFile;
    } catch (IOException caught) {
      throw new RuntimeException(caught);
    }
  }

  public ParseUnit getParseUnit(InputFile file) {
    ParseUnit unit = null;
    try {
      unit = new ParseUnit(file.inputStream(), file.filename(), session, file.charset());
      unit.treeParser01();
    } catch (IOException caught) {
      throw new RuntimeException("Unable to parse file", caught);
    }

    return unit;
  }
}
