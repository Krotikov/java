import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.IReader;
import com.java_polytech.pipeline_interfaces.IWriter;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Manager {
    IReader reader;
    IWriter writer;
    IExecutor executor;
    PipelineParams params;
    InputStream inStream;
    OutputStream outStream;

    class PipelineParams {
        public final String inFile;
        public final String outFile;
        public final String readerClassName;
        public final String writerClassName;
        public final String executorClassName;
        public final String readerConfigFile;
        public final String writerConfigFile;
        public final String executorConfigFile;

        PipelineParams(String inFile, String outFile, String readerClassName,
                       String writerClassName, String executorClassName, String readerConfigFile,
                       String writerConfigFile, String executorConfigFile){
            this.inFile = inFile;
            this.outFile = outFile;
            this.readerClassName = readerClassName;
            this.writerClassName = writerClassName;
            this.executorClassName = executorClassName;
            this.readerConfigFile = readerConfigFile;
            this.writerConfigFile = writerConfigFile;
            this.executorConfigFile = executorConfigFile;

        }
    }

    RC SetConfig(String configName){
        MConfig config = new MConfig();
        RC rc = config.readConfig(configName);
        if(!rc.isSuccess())
            return rc;

        params = new PipelineParams(config.inputFile, config.outputFile, config.readerClassName,
                config.writerClassName, config.executorClassName, config.readerConfigFile,
                config.writerConfigFile, config.executorConfigFile);

        return rc;
    }

    RC CheckParams(){
        if(!Files.exists(Paths.get(params.inFile))){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        if(!Files.exists(Paths.get(params.readerConfigFile))){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        if(!Files.exists(Paths.get(params.executorConfigFile))){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }
        if(!Files.exists(Paths.get(params.writerConfigFile))){
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;
        }

        return RC.RC_SUCCESS;
    }

    RC OpenFiles(){
        try{
            inStream = new FileInputStream(params.inFile);
        }catch (FileNotFoundException e){
            return RC.RC_MANAGER_INVALID_INPUT_FILE;
        }

        try{
            outStream = new FileOutputStream(params.outFile);
        }catch (FileNotFoundException e){
            return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
        }

        return RC.RC_SUCCESS;
    }

    RC CloseFiles(){
        try {
            inStream.close();
        } catch (IOException ex) {
            return RC.RC_MANAGER_INVALID_INPUT_FILE; //TODO add close
        }
        try {
            outStream.close();
        } catch (IOException ex) {
            return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
        }
        return RC.RC_SUCCESS;
    }

    RC GetClassesByName(){
        Class<?> tmp;
        try{
            tmp = Class.forName(params.readerClassName);
            if(IReader.class.isAssignableFrom(tmp))
                reader = (IReader) tmp.getDeclaredConstructor().newInstance();
            else
                return RC.RC_MANAGER_INVALID_READER_CLASS;

        }catch(Exception e) {
            return RC.RC_MANAGER_INVALID_READER_CLASS;
        }

        try {
            tmp = Class.forName(params.writerClassName);
            if (IWriter.class.isAssignableFrom(tmp))
                writer = (IWriter) tmp.getDeclaredConstructor().newInstance();
            else
                return RC.RC_MANAGER_INVALID_WRITER_CLASS;
        }
        catch (Exception e) {
            return RC.RC_MANAGER_INVALID_WRITER_CLASS;
        }

        try {
            tmp = Class.forName(params.executorClassName);
            if (IExecutor.class.isAssignableFrom(tmp))
                executor = (IExecutor) tmp.getDeclaredConstructor().newInstance();
            else
                return RC.RC_MANAGER_INVALID_EXECUTOR_CLASS;
        }
        catch (Exception e) {
            return RC.RC_MANAGER_INVALID_EXECUTOR_CLASS;
        }

        return RC.RC_SUCCESS;
    }

    RC Run(String configName){
        RC rc;
        rc = SetConfig(configName);
        if(!rc.isSuccess()){
            return rc;
        }

        if(!(rc = CheckParams()).isSuccess())
            return rc;
        if (!(rc = OpenFiles()).isSuccess())
            return rc;
        if (!(rc = GetClassesByName()).isSuccess())
            return rc;
        if (!(rc = reader.setConsumer(executor)).isSuccess())
            return rc;
        if (!(rc = executor.setConsumer(writer)).isSuccess())
            return rc;
        if (!(rc = reader.setConfig(params.readerConfigFile)).isSuccess())
            return rc;
        if (!(rc = executor.setConfig(params.executorConfigFile)).isSuccess())
            return rc;
        if (!(rc = writer.setConfig(params.writerConfigFile)).isSuccess())
            return rc;
        if (!(rc = reader.setInputStream(inStream)).isSuccess())
            return rc;
        if (!(rc = writer.setOutputStream(outStream)).isSuccess())
            return rc;

        rc = reader.run();
        if(!rc.isSuccess())
            return rc;

        rc = CloseFiles();
        return rc;
    }
}
