package strategy.navigation.potentialFieldNavigation.fieldsources;

import strategy.navigation.potentialFieldNavigation.PotentialField;
import strategy.navigation.potentialFieldNavigation.PotentialSource;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class SourceGroup {
    private LinkedList<PotentialSource> lines;

    public SourceGroup(){
        this.lines = new LinkedList<PotentialSource>();
    }

    public void addSource(PotentialSource line){
        this.lines.add(line);
    }

    public void addToField(PotentialField field){
        for(PotentialSource s : this.lines){
            field.addSource(s);
        }
    }

    @Override
    public String toString(){
        String res = "";
        for(PotentialSource s : this.lines){
            res = res + s + "\n";
        }
        return res;
    }
}
