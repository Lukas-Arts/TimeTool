package lynx.TimeTool.GUI.diagram;

public class Value implements Comparable<Value>{
    private Comparable v;
    public Value(Comparable v){
        this.v=v;
    }
    public String toString(){
        return v.toString();
    }
    public Comparable getValue(){
        return v;
    }
    public void setValue(Comparable v){
        this.v=v;
    }
    @Override
    public int compareTo(Value o) {
        return v.compareTo(o.getValue());
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Value){
            if(this.compareTo((Value)o)==0){
                return true;
            }else return false;
        }else return false;
    }
    public Value copy(){
        return new Value(v);
    }
}
