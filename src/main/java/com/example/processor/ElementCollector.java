package com.example.processor;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner8;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bfsan
 * @since 2020/5/4
 **/
public class ElementCollector extends ElementScanner8<Void, Void> {
    private List<ExecutableElement> constructs;
    private List<VariableElement> injectableVariable;

    public ElementCollector init() {
        constructs = new ArrayList<>();
        injectableVariable = new ArrayList<>();
        return this;
    }

    @Override
    public Void visitVariable(VariableElement e, Void aVoid) {
        if (e.getAnnotation(Autowired.class) != null || e.getAnnotation(Resource.class) != null) {
            injectableVariable.add(e);
        }
        return super.visitVariable(e, aVoid);
    }


    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (ElementKind.CONSTRUCTOR == e.getKind()) {
            constructs.add(e);
        }
        return super.visitExecutable(e, aVoid);
    }


    public List<ExecutableElement> getConstructs() {
        return constructs;
    }

    public void setConstructs(List<ExecutableElement> constructs) {
        this.constructs = constructs;
    }

    public List<VariableElement> getInjectableVariable() {
        return injectableVariable;
    }

    public void setInjectableVariable(List<VariableElement> injectableVariable) {
        this.injectableVariable = injectableVariable;
    }
}
