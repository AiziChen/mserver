package org.quanye.sobj.struct;

import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InValidSObjSyntaxException;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

import java.lang.reflect.InvocationTargetException;

/**
 * Cons
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class SObjNode {
    private String nodeValue;
    private SObjNode car;
    private SObjNode cdr;

    public SObjNode(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public SObjNode getNode(String key) {
        SObjNode node = getCdr();
        while (true) {
            if (node.getCar().nodeValue.equals(key)) {
                return node;
            } else {
                node = node.getCdr();
                if (node == null) {
                    return null;
                }
            }
        }
    }

    public <T> T getValue(Class<T> clazz) {
        String keysValue = S$.car(S$.cdr(nodeValue));
        if (S$.isPair(keysValue)) {
            try {
                return SObjParser.toObject(keysValue, clazz);
            } catch (InValidSObjSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return clazz.getConstructor(String.class).newInstance(C$.trimStr(keysValue));
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public SObjNode getCar() {
        return car;
    }

    public void setCar(SObjNode car) {
        this.car = car;
    }

    public SObjNode getCdr() {
        return cdr;
    }

    public void setCdr(SObjNode cdr) {
        this.cdr = cdr;
    }

    @Override
    public String toString() {
        return "Cons{" +
                "carValue='" + nodeValue + '\'' +
                ", car=" + car +
                ", cdr=" + cdr +
                '}';
    }
}