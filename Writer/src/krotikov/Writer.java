package krotikov;

import com.java_polytech.pipeline_interfaces.IWriter;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

import configKrot.*;

public class Writer implements IWriter{
    private BufferedOutputStream bufOutput;
    private OutputStream outStream;
    private int bufSize = 0;

    private static final int availableSize = 1000000;

    class WriterGrammar extends Grammar {
        static final String BufSize = "BufferSize";
        @Override
        protected void setGrammar() {
            this.grammarList.add(BufSize);
        }
    }

    RC CheckValidParams(String param){
        int tmp = 0;
        try{
            tmp = Integer.parseInt(param);
        }
        catch (NumberFormatException ex){
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }

        if(tmp > 0 && tmp <= availableSize){
            bufSize = tmp;
            return RC.RC_SUCCESS;
        }

        return RC.RC_WRITER_CONFIG_SEMANTIC_ERROR;
    }

    @Override
    public RC setOutputStream(OutputStream outputStream) {
        if (outputStream == null)
            return RC.RC_WRITER_FAILED_TO_WRITE;

        this.outStream = outputStream;
        if(bufSize != 0)
            bufOutput = new BufferedOutputStream(this.outStream, bufSize);
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        Grammar grammar = new WriterGrammar();
        ConfigAnalyzer confAnal = new ConfigAnalyzer(s, grammar, RC.RCWho.WRITER);
        RC rc = confAnal.ReadConfig();
        if(!rc.isSuccess())
            return rc;

        rc = CheckValidParams(confAnal.configElements.get(WriterGrammar.BufSize));
        if(!rc.isSuccess())
            return rc;

        if(outStream != null && bufOutput == null)
            bufOutput = new BufferedOutputStream(this.outStream, bufSize);
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


