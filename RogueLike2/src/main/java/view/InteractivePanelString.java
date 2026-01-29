package view;

public class InteractivePanelString {
    String inputString;
    Runnable action;
    private int maxStringSize;

    public InteractivePanelString(int maxStringSize, Runnable action){
        this.action = action;
        inputString = "";
        this.maxStringSize = maxStringSize;
    }

    void run(){
        action.run();
    }

    public String getText(){
        return inputString;
    }

    public void setText(String text){
        inputString = text;
    }

    public int getMaxStringSize(){
        return maxStringSize;
    }
}
