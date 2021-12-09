import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IWriter;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

public class Writer implements IWriter{
    BufferedOutputStream bufOutput;
    PipelineParams params;
    int bufSize;

    static final int availableSize = 1000000;

    class PipelineParams {
        public final String BufSize;

        PipelineParams(String BufSize) {
            this.BufSize = BufSize;
        }
    }

    RC CheckValidParams(){
        int size = Integer.parseInt(params.BufSize);
        if(size < availableSize && size > 0){
            bufSize = size;
            return RC.RC_SUCCESS;
        }
        return RC.RC_WRITER_CONFIG_SEMANTIC_ERROR;
    }

    @Override
    public RC setOutputStream(OutputStream outputStream) {
        if (outputStream == null)
            return RC.RC_WRITER_FAILED_TO_WRITE;
        if(bufSize != 0)
            bufOutput = new BufferedOutputStream(outputStream, bufSize);
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        WConfig config = new WConfig();
        RC rc = config.ReadConfig(s);
        if(!rc.isSuccess())
            return rc;

        params = new PipelineParams(config.bufSize);
        rc = CheckValidParams();
        if(!rc.isSuccess())
            return rc;

        return RC.RC_SUCCESS;
    }

    @Override
    public RC consume(byte[] bytes) {
        if(bytes == null) {
            try{
                bufOutput.flush();
            }
            catch (IOException e) {
                return RC.RC_WRITER_FAILED_TO_WRITE;
            }
            return RC.RC_SUCCESS;
        }
        try{
            bufOutput.write(bytes);
        }
        catch (IOException e){
            return RC.RC_WRITER_FAILED_TO_WRITE;
        }
        return RC.RC_SUCCESS;
    }

}


