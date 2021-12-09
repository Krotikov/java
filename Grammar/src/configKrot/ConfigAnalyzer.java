package configKrot;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ConfigAnalyzer {
    Grammar grammar;
    final String configPath;
    final protected RC.RCWho who;
    public HashMap<String, String> configElements;

    public ConfigAnalyzer(String configPath, Grammar grammar, RC.RCWho who) {
        this.configPath = configPath;
        this.grammar = grammar;
        this.who = who;
        configElements = new HashMap<>();
    }

    /**
     * BRIEF:
     * read config file
     * ARGS:
     * None
     * RETURN:
     * None
     */
    public RC ReadConfig() {
        grammar.setGrammar();
        try {
            BufferedReader bufRead = new BufferedReader(new FileReader(configPath));
            String readLine;

            while ((readLine = bufRead.readLine()) != null) {
                String[] elems = readLine.split(Grammar.splitter);

                /* check left param in config file */
                if (elems.length == 2) {
                    String leftPart = elems[0].trim();

                    String key = this.grammar.getKeyGrammar(leftPart);
                    if (key == null || configElements.containsKey(leftPart)) {
                        return new RC(who, RC.RCType.CODE_CONFIG_GRAMMAR_ERROR, "Input stream error in config file");
                    }
                    configElements.put(key, elems[1].trim());

                } else {
                    return new RC(who, RC.RCType.CODE_CONFIG_GRAMMAR_ERROR, "Input stream error in config file");
                }
            }

            if (configElements.size() != this.grammar.getSizeGrammarList()) {
                return new RC(who, RC.RCType.CODE_CONFIG_GRAMMAR_ERROR, "Input stream error in config file");
            }

            bufRead.close();
        } catch (IOException ex) {
            return new RC(who, RC.RCType.CODE_CONFIG_FILE_ERROR, "Input stream error in config file");
        }
        return RC.RC_SUCCESS;
    }
}
