import com.java_polytech.pipeline_interfaces.RC;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class WConfig {
    static final String BUFFER_SIZE = "BufferSize";
    String bufSize;
    private static final String splitter = "=";
    private static final String empty = "";
    private static final String space = "\\s";


    WConfig(){
        bufSize = null;
    }

    RC ReadConfig (String filename){
        try(FileReader fileReader = new FileReader(filename)){
            Scanner scanner = new Scanner (fileReader);
            final int paramInd = 0;
            final int valueInd = 1;

            String line;
            try{
                line = scanner.nextLine();
            }catch(Exception e){
                return RC.RC_WRITER_CONFIG_FILE_ERROR;
            }

            if(scanner.hasNext())
                return RC.RC_WRITER_CONFIG_GRAMMAR_ERROR;

            String[] splitLine = line.split(splitter);
            if(splitLine.length != 2)
                return RC.RC_WRITER_CONFIG_GRAMMAR_ERROR;

            splitLine[paramInd] = splitLine[paramInd].replaceAll(space, empty);
            splitLine[valueInd] = splitLine[valueInd].replaceAll(space, empty);

            if(splitLine[paramInd].equals(BUFFER_SIZE)){
                bufSize = splitLine[valueInd];
            }
            else
                return RC.RC_WRITER_CONFIG_GRAMMAR_ERROR;

            return RC.RC_SUCCESS;

        }catch(IOException e){
            return RC.RC_WRITER_CONFIG_FILE_ERROR;
        }
    }

}
