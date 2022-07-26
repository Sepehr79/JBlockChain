package org.sepehr.jblockchain.sample;

import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.block.BlockBody;

import java.nio.charset.StandardCharsets;

public class Text implements BlockBody {

    private final String text;

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String hash() {
        return Hashing.sha256()
                .hashString(this.text, StandardCharsets.UTF_8)
                .toString();
    }

    @Override
    public String toString() {
        return "Text{" +
                "text='" + text + '\'' +
                '}';
    }
}
