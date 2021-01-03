package ua.demirug.vkauth;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class HashManager {
    
    private HashFunction hf;

    public HashManager(String type) {
        switch (type.toUpperCase()) {
            case "MD5": {
                this.hf = Hashing.md5();
                break;
            }
            case "SHA256": {
                this.hf = Hashing.sha256();
                break;
            }
            case "CRC32": {
                this.hf = Hashing.crc32();
                break;
            }
        }
    }

    public String toHash(String string) {
        if (this.hf == null) {
            return string;
        }
        return this.hf.newHasher().putString((CharSequence)string, Charsets.UTF_8).hash().toString();
    }

    public boolean hasEnable() {
        return this.hf != null;
    }
}

