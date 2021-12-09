import com.java_polytech.pipeline_interfaces.RC;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class EConfig {
    private static final String splitter = "=";
    private static final String numStr = "NUM";
    private static final String modeStr = "MODE";
    private static final int paramsInitLen = 2;

    String num;
    String mode;

    EConfig(){
        num = null;
        mode = null;
    }

    private RC SetParams(String line) {
        final int name = 0;
        final int value = 1;
        String[] params = line.split(splitter);

        if (params.length < paramsInitLen) {
            return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }

        switch (params[name]) {
            case numStr:
                num = params[value];
                break;
            case modeStr:
                mode = params[value];
                break;
            default:
                return RC.RC_EXECUTOR_CONFIG_GRAMMAR_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    RC readConfig(String filename) {
        try (FileReader fileReader = new FileReader(filename)) {
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                RC rc = SetParams(line);
                if (rc != RC.RC_SUCCESS)
                    return rc;
            }

            if (num == null)
                return RC.RC_EXECUTOR_CONFIG_GRAMMAR_ERROR;
        } catch (IOException e) {
            return RC.RC_EXECUTOR_CONFIG_FILE_ERROR;
        }
        return RC.RC_SUCCESS;
    }
}
