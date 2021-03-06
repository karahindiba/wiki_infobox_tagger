package edu.yildiz.nlp.sequence.tagger.Run;

import edu.yildiz.nlp.sequence.tagger.*;
import org.apache.commons.cli.*;

import java.util.List;


/**
 * Named entity tagger
 * @author Delip Rao
 *
 */
public class WikiRelationTagger {

  /**
   * @param args
   */
  static Tester tester;
  public static void main(String[] args) throws Exception {
    CommandLineParser parser = new GnuParser();
    Options options = WikiRelationTaggerUtils.buildOptions();
    CommandLine commandLine = parser.parse(options, args);
    execute(options, commandLine);
  }

  private static void execute(Options options, CommandLine commandLine) throws Exception {
    
    if(commandLine.hasOption("help") || commandLine.getOptions().length == 0) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "WikiTagger", options);
      System.exit(-1);
    }
    if(commandLine.hasOption("model") == false) 
      throw new Exception("Model file should be specified.");
    
    CRFSequenceLearnerOptions defaultOptions = new CRFSequenceLearnerOptions();
    WikiRelationTaggerUtils.updateLearnerOptions(commandLine, defaultOptions);
    CRFSequenceLearner sequenceLearner = new CRFSequenceLearner(defaultOptions);
    
    if(commandLine.hasOption("train")) {
      Trainer trainer = new Trainer(sequenceLearner);
      trainer.train(commandLine.getOptionValue("train"));
      trainer.saveModel(commandLine.getOptionValue("model"));
    }

    if(commandLine.hasOption("test")) {
      Tester tester = new Tester(sequenceLearner);
      tester.loadModel(commandLine.getOptionValue("model"));
      if(commandLine.hasOption("evaluate")) {
        SequenceEvaluator sequenceEvaluator = new SequenceEvaluator(
            new String[] { "p_dogum_yer" },
            new String[] { "p_dogum_yer" }
            );
        tester.setEvaluator(sequenceEvaluator);
        tester.evaluate(commandLine.getOptionValue("test"));
      }
      else
          tester.classify(commandLine.getOptionValue("test"), new WikiOutputCallback());
    }
  }

    public static List<ResultSet> test(String testString) throws Exception {
        tester.classifyText(testString, new WikiOutputCallback());
        return CRFSequenceLearner.resultSet;
    }
    public static void LoadModel(String modelFile) throws Exception {
        CRFSequenceLearnerOptions defaultOptions = new CRFSequenceLearnerOptions();
        CRFSequenceLearner sequenceLearner = new CRFSequenceLearner(defaultOptions);
        tester = new Tester(sequenceLearner);
        tester.loadModel(modelFile);
    }

}
