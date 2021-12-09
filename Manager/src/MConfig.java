import com.java_polytech.pipeline_interfaces.RC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class MConfig {

    private static final String inputFileStr = "InputFile";
    private static final String outputFileStr = "OutputFile";
    private static final String readerClassNameStr = "ReaderClassName";
    private static final String writerClassNameStr = "WriterClassName";
    private static final String executorClassNameStr = "ExecutorClassName";
    private static final String readerConfigFileStr = "ReaderConfigFile";
    private static final String writerConfigFileStr = "WriterConfigFile";
    private static final String executorConfigFileStr = "ExecutorConfigFile";
    private static final String splitter = "="; //Splitter for config
    private static final int paramsInitLen = 2;

    /*public enum Params{//Keywords for parameters
        InputFile,
        OutputFile,
        ReaderClassName,
        WriterClassName,
        ExecutorClassName,
        ReaderConfigFile,
        WriterConfigFile,
        ExecutorConfigFile
    }*/

    String inputFile;
    String outputFile;
    String readerClassName;
    String writerClassName;
    String executorClassName;
    String readerConfigFile;
    String writerConfigFile;
    String executorConfigFile;

    MConfig (){
        inputFile = null;
        outputFile = null;
        readerClassName = null;
        writerClassName = null;
        executorClassName = null;
        readerConfigFile = null;
        writerConfigFile = null;
        executorConfigFile = null;
    }

    private RC SetParams(String line){
        final int name = 0;
        final int value = 1;
        String[] params = line.split(splitter);

        if(params.length < paramsInitLen) {
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        switch(params[name]){
            case inputFileStr:
                inputFile = params[value];
                break;
            case outputFileStr:
                outputFile = params[value];
                break;
            case readerClassNameStr:
                readerClassName = params[value];
                break;
            case writerClassNameStr:
                writerClassName = params[value];
                break;
            case executorClassNameStr:
                executorClassName = params[value];
                break;
            case readerConfigFileStr:
                readerConfigFile = params[value];
                break;
            case writerConfigFileStr:
                writerConfigFile = params[value];
                break;
            case executorConfigFileStr:
                executorConfigFile = params[value];
                break;
            default:
                return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    RC readConfig(String filename){
        try (FileReader fileReader = new FileReader(filename)){
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                RC rc = SetParams(line);
                if(rc != RC.RC_SUCCESS)
                    return rc;
            }

            if (inputFile == null || outputFile == null || readerClassName == null
                || writerClassName == null || executorClassName == null || readerConfigFile == null
                || writerConfigFile == null || executorConfigFile == null)
                return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;

        }catch(IOException e){
            return RC.RC_MANAGER_CONFIG_FILE_ERROR;
        }
        return RC.RC_SUCCESS;
    }

}
