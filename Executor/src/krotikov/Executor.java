package krotikov;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.RC;

import configKrot.*;

import java.util.Collections;

public class Executor implements IExecutor{
    private final int MaxNum = 16777;

    private ExecutorGrammar.MODE mode;
    private int num;
    private IConsumer consumer;

    private static class ExecutorGrammar extends Grammar{
        public enum MODE{
            CODE("CODE"),
            DECODE("DECODE");

            private final String type;
            MODE(String type){
                this.type = type;
            }
            public String GetTypeMode(){
                return this.type;
            }
        }

        static final String mode = "MODE";
        static final String num = "NUM";

        @Override
        protected void setGrammar() {
            this.grammarList.add(mode);
            this.grammarList.add(num);
        }
    }

    RC CheckParams(String mode, String numStr){
        if(mode.equals(ExecutorGrammar.MODE.CODE.GetTypeMode()))
            this.mode = ExecutorGrammar.MODE.CODE;
        else if(mode.equals(ExecutorGrammar.MODE.DECODE.GetTypeMode()))
            this.mode = ExecutorGrammar.MODE.DECODE;
        else
            return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;

        try{
            num = Integer.parseInt(numStr);
            if(num > MaxNum)
                return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }catch(NumberFormatException e){
            return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        Grammar grammar = new ExecutorGrammar();
        ConfigAnalyzer confAnal = new ConfigAnalyzer(s, grammar, RC.RCWho.EXECUTOR);
        RC rc = confAnal.ReadConfig();
        if(!rc.isSuccess())
            return rc;

        rc = CheckParams(confAnal.configElements.get(ExecutorGrammar.mode),
                         confAnal.configElements.get(ExecutorGrammar.num));
        if(!rc.isSuccess())
            return rc;

        return RC.RC_SUCCESS;
    }

    @Override
    public RC consume(byte[] bytes) {
        if(bytes == null){
            consumer.consume(null);
        }
        else{
            byte[] buffer;
            Encoder encoder = new Encoder(num);
            if(mode == ExecutorGrammar.MODE.CODE){
               buffer = encoder.Code(bytes);
            }
            else{
                buffer = encoder.Decode(bytes);
            }
            if(buffer != null) {
                consumer.consume(buffer);
            }
        }

        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConsumer(IConsumer iConsumer) {
        if(iConsumer == null)
            return RC.RC_MANAGER_INVALID_EXECUTOR_CLASS;
        consumer = iConsumer;
        return RC.RC_SUCCESS;
    }
}