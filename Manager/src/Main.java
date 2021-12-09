import com.java_polytech.pipeline_interfaces.RC;

public class Main {
    static final int FILENAME_IND = 0;

    public static void main(String[] args)  {

        if(args[FILENAME_IND] != null) {
            Manager manager = new Manager(); //Get config
            RC rc = manager.Run(args[FILENAME_IND]);
            if(!rc.isSuccess()){
                System.out.println(rc.info);
            }
            else{
                System.out.println(RC.RC_SUCCESS.info);
            }
        }else{
            System.out.println(RC.RC_MANAGER_INVALID_ARGUMENT.info);
        }
    }
}
