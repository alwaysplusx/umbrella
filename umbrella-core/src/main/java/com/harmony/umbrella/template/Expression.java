package com.harmony.umbrella.template;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wuxii
 */
@Getter
@Setter
@NoArgsConstructor
public class Expression implements Serializable {

    private String text;
    private boolean plainText;

    public Expression(String text, boolean plainText) {
        this.text = text;
        this.plainText = plainText;
    }

    @Override
    public String toString() {
        return text;
    }
}
