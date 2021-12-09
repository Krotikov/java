package configKrot;

import java.util.ArrayList;

public abstract class Grammar {
    static final String splitter = "=";
    public final ArrayList<String>  grammarList = new ArrayList<>();

    protected abstract void setGrammar();

    String getKeyGrammar(String value){
        for(String gr : grammarList){
            if(gr.equalsIgnoreCase(value)){
                return gr;
            }
        }
        return null;
    }

    int getSizeGrammarList(){
        return grammarList.size();
    }
}
