import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IReader;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.IOException;
import java.io.InputStream;

public class Reader implements IReader {
    InputStream inStream;
    byte[] buffer;
    int bufSize;
    boolean streamEnd = false; //Check of end file (true - end of fail, false - not)
    IConsumer consumer;
    PipelineParams params;

    static final int availableSize = 1000000;
    class PipelineParams{
        public final String BufSize;

        PipelineParams(String BufSize) {
            this.BufSize = BufSize;
        }
    }

    RC CheckValidParams(){
        int size = Integer.parseInt(params.BufSize);
        if(size < availableSize && size > 0) {
            bufSize = size;
            return RC.RC_SUCCESS;
        }
        return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
    }

    boolean EndFile(){
        return streamEnd;
    }

    void Read() throws IOException {
        if(inStream.available() < bufSize){
            buffer = new byte[inStream.available()];
            streamEnd = true;
        }else{
            buffer = new byte[bufSize];
        }
        inStream.read(buffer);
    }

    @Override
    public RC setInputStream(InputStream inputStream) {
        if(inputStream == null)
            return RC.RC_READER_FAILED_TO_READ;

        this.inStream = inputStream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC run() {
        while(!EndFile()) {
            try {
                Read();
            } catch (IOException e) {
                return RC.RC_READER_FAILED_TO_READ;
            }

            RC rc = consumer.consume(buffer);
            if (!rc.isSuccess())
                return rc;
        }
        RC rc = consumer.consume(null);
        if(!rc.isSuccess())
            return rc;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        RConfig config = new RConfig();
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
    public RC setConsumer(IConsumer iConsumer) {
        consumer = iConsumer;
        return RC.RC_SUCCESS;
    }
}
