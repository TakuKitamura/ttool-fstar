package avatartranslator.modelchecker;

import java.util.List;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarTransition;

public class SpecificationActionLoop {
    private List<AvatarTransition> path;
    private SafetyProperty reachability;
    private SafetyProperty property;
    private boolean hasProperty;
    private boolean result;
    private boolean error;

    public SpecificationActionLoop(List<AvatarTransition> path, AvatarSpecification spec) {
        this.path = path;
        this.error = false;
        init(spec);
    }
    
    private void init(AvatarSpecification spec) {
        //check presence of unguarded loops
        boolean guarded = false;
        StringBuilder condition = new StringBuilder("!(");
        for (AvatarTransition at : path) {
            if (at.isGuarded()) {
                String guard = at.getGuard().toString().replaceAll("\\[", "").trim().replaceAll("\\]", "");
                if (guarded) {
                    condition.append(" && " + guard);
                } else {
                    condition.append(guard);
                }   
                guarded = true;
            }
        }
        condition.append(")");
        AvatarTransition at = path.get(path.size() - 1);
        //no transition has a guard check --> reachability of first state
        reachability = new SafetyProperty("E<> " + at.getBlock().getName() + "." + at.getNext(0).getName());
        reachability.analyzeProperty(spec);
        error = reachability.hasError();
        hasProperty = false;
        if (guarded) {
            hasProperty = true;
            property = new SafetyProperty("E<> " + at.getNext(0).getName() + " && " + condition.toString());
            property.analyzeProperty((AvatarBlock) at.getBlock(), spec);
            error |= property.hasError();
        }
    }
    
    public boolean hasError() {
        return error;
    }
    
    public boolean hasProperty() {
        return hasProperty;
    }
    
    public SafetyProperty getReachability() {
        return reachability;
    }
    
    public SafetyProperty getProperty() {
        return property;
    }
    
    public void setResult() {
        result = reachability.result;
        if (hasProperty) {
            result &= !property.result;
        }
    }
    
    public boolean getResult() {
        return result;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        
        s.append("In block " + path.get(path.size() - 1).getBlock().getName() + " : " + path.get(path.size() - 1).getNext(0).getName());
        for (AvatarTransition at : path) {
            s.append(" --> " + at.getNext(0).getName());
        }
        
        return s.toString();
    }
}
