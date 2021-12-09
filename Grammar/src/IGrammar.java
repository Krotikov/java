package java_polytech.leo.universal_config;
import com.java_polytech.pipeline_interfaces.IGrammar;

public interface IGrammar {
    int getSize();
    boolean contains(String lexeme);
}