package lynx.TimeTool.GUI.diagram;

import java.awt.*;

public class DataItem {
    private Value x,y;
    private Color color=Color.BLACK;
    public DataItem(Value x, Value y){
        this.x=x;
        this.y=y;
    }
    public DataItem(Value x,Value y,Color c){
        this(x,y);
        this.color=c;
    }
    public Value getX() {
        return x;
    }
    public void setX(Value x) {
        this.x = x;
    }
    public Value getY() {
        return y;
    }
    public void setY(Value y) {
        this.y = y;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
}
