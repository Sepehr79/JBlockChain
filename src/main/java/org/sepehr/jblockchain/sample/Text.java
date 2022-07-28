package org.sepehr.jblockchain.sample;

import org.sepehr.jblockchain.block.BlockBody;

public class Text implements BlockBody {

    private final String text;

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Text{" +
                "text='" + text + '\'' +
                '}';
    }
}
