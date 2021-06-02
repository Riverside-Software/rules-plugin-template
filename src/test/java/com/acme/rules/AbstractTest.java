package com.acme.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.prorefactor.core.schema.Schema;
import org.prorefactor.refactor.RefactorSession;
import org.prorefactor.refactor.settings.ProparseSettings;
import org.prorefactor.treeparser.ParseUnit;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.internal.google.common.io.Files;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.openedge.api.Constants;
import org.sonar.plugins.openedge.api.objects.DatabaseWrapper;
import org.testng.annotations.BeforeMethod;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.progress.xref.CrossReference;

import eu.rssw.antlr.database.objects.DatabaseDescription;
import eu.rssw.pct.RCodeInfo;
import eu.rssw.pct.RCodeInfo.InvalidRCodeException;

public abstract class AbstractTest {
  private static final String BASEDIR = "src/test/resources/";

  protected SensorContextTester context;
  protected RuleKey ruleKey;

  private RefactorSession session;
  private Schema schema;
  private JAXBContext jaxbContext;
  private SAXParserFactory saxParserFactory;
  private Unmarshaller unmarshaller;

  
  // FIXME Should be BeforeTest
  @BeforeMethod
  public void initContext() throws IOException {
    ruleKey = RuleKey.parse(Constants.RSSW_REPOSITORY_KEY + ":" + this.getClass().getName());

    saxParserFactory = SAXParserFactory.newInstance();
    saxParserFactory.setNamespaceAware(false);

    try {
      saxParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      jaxbContext = JAXBContext.newInstance("com.progress.xref", CrossReference.class.getClassLoader());
      unmarshaller = jaxbContext.createUnmarshaller();
    } catch (ParserConfigurationException | JAXBException | SAXNotRecognizedException
        | SAXNotSupportedException caught) {
      throw new IllegalStateException(caught);
    }

    try (FileInputStream input = new FileInputStream("src/test/resources/sp2k.schema")) {
      schema = new Schema(new DatabaseWrapper(DatabaseDescription.deserialize(input, "sp2k")));
      session = new RefactorSession(new ProparseSettings("src/test/resources"), schema);
    }

    // Inject all rcodes
    for (File f : Files.fileTreeTraverser().preOrderTraversal(new File("src/test/resources"))) {
      if (f.isFile() && f.getName().endsWith(".r")) {
        try (InputStream is = new FileInputStream(f)) {
          RCodeInfo rci = new RCodeInfo(is);
          session.injectTypeInfo(rci.getTypeInfo());
        } catch (IOException | InvalidRCodeException uncaught) {
          // No-op
        }
      }
    }
  }

  @BeforeMethod
  public void initTest() throws IOException {
    context = SensorContextTester.create(new File(BASEDIR));

    // Common include files have to be referenced in the FileSystem object
    for (File f : new File(BASEDIR + "inc").listFiles()) {
      context.fileSystem().add(
          TestInputFileBuilder.create(BASEDIR, "inc/" + f.getName()).setLanguage(Constants.LANGUAGE_KEY).setType(
              Type.MAIN).setCharset(Charset.defaultCharset()).setContents(
                  Files.toString(new File(BASEDIR, "inc/" + f.getName()), Charset.defaultCharset())).build());
    }
  }

  public InputFile getInputFile(String file) {
    try {
      InputFile inputFile = TestInputFileBuilder.create(BASEDIR, file).setLanguage(Constants.LANGUAGE_KEY).setType(
          Type.MAIN).setCharset(Charset.defaultCharset()).setContents(
              Files.toString(new File(BASEDIR, file), Charset.defaultCharset())).build();
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
      CrossReference xref = jaxbXREF(new File("src/test/resources/" + file.filename() + ".xref"));;
      System.out.println(file.filename());
      unit.attachXref(xref);
      unit.treeParser01();
      unit.attachTypeInfo(session.getTypeInfo(unit.getRootScope().getClassName()));
    } catch (IOException caught) {
      throw new RuntimeException("Unable to parse file", caught);
    }
    return unit;
  }

  private CrossReference jaxbXREF(File xrefFile) {
    CrossReference doc = null;
    if ((xrefFile != null) && xrefFile.exists()) {
      try (InputStream inpStream = new FileInputStream(xrefFile)) {
        long startTime = System.currentTimeMillis();
        InputSource is = new InputSource(inpStream);
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();
        SAXSource source = new SAXSource(reader, is);
        doc = (CrossReference) unmarshaller.unmarshal(source);
      } catch (JAXBException | SAXException | ParserConfigurationException | IOException caught) {
        System.out.println("Unable to parse XREF file " + xrefFile.getAbsolutePath());
      }
    }

    return doc;
  }

}
