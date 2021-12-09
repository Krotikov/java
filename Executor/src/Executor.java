import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.RC;

public class Executor implements IExecutor{
    enum MODE {
        CODE,
        DECODE
    }

    private static final String codeStr = "CODE";
    private static final String decodeStr = "DECODE";
    private final int MaxNum = 16777;

    MODE mode;
    int num;
    IConsumer consumer;
    PipelineParams params;

    class PipelineParams {
        public final String mode;
        public final String num;

        PipelineParams(String modeStr, String numStr){
            this.mode = modeStr;
            this.num = numStr;
        }
    }

    RC CheckParams(){
        if(params.mode.equals(MODE.CODE.toString()))
            mode = MODE.CODE;
        else if(params.mode.equals(MODE.DECODE.toString()))
            mode = MODE.DECODE;
        else
            return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;

        try{
            num = Integer.parseInt(params.num);
            if(num > MaxNum)
                return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }catch(NumberFormatException e){
            return RC.RC_EXECUTOR_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String s) {
        EConfig config = new EConfig();
        RC rc = config.readConfig(s);
        if(!rc.isSuccess())
            return rc;

        params = new PipelineParams(config.mode, config.num);
        rc = CheckParams();
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
            if(mode == MODE.CODE){
               buffer = encoder.Code(bytes);
            }
            else{
                buffer = encoder.Decode(bytes);
            }
            consumer.consume(buffer);
        }

        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConsumer(IConsumer iConsumer) {
        consumer = iConsumer;
        return RC.RC_SUCCESS;
    }
}
