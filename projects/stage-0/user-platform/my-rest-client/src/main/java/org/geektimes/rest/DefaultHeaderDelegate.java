package org.geektimes.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

public class DefaultHeaderDelegate implements RuntimeDelegate.HeaderDelegate {
    @Override
    public Object fromString(String value) {
        return null;
    }

    @Override
    public String toString(Object value) {
        String res = null;
        // process MediaType
        if (value instanceof MediaType) {
            res = ((MediaType) value).getType() + "/" + ((MediaType) value).getSubtype();
        } else {
            // TODO: process other types
        }
        return res;
    }
}
