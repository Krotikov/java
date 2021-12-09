package krotikov;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IReader;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.IOException;
import java.io.InputStream;

import configKrot.*;

public class Reader implements IReader {
    private InputStream inStream;
    private byte[] buffer;
    private int bufSize;
    private boolean streamEnd = false; //Check of end file (true - end of fail, false - not)
    private IConsumer consumer;

    static final int availableSize = 1000000;
    class ReaderGrammar extends Grammar {
        static final String bufferSize = "BufferSize";
        @Override
        protected void setGrammar() {
            this.grammarList.add(bufferSize);
        }
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

    RC checkValidOfBuffer(String buffer){
        int tmpSize;
        try{
            tmpSize = Integer.parseInt(buffer);
        }
        catch (NumberFormatException ex){
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }

        if(tmpSize > 0 && tmpSize <= availableSize){
            bufSize = tmpSize;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        Grammar grammar = new ReaderGrammar();
        ConfigAnalyzer confAnal = new ConfigAnalyzer(s, grammar, RC.RCWho.READER);
        RC rc = confAnal.ReadConfig();
        if(!rc.isSuccess())
            return rc;

        rc = checkValidOfBuffer(confAnal.configElements.get(ReaderGrammar.bufferSize));
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
