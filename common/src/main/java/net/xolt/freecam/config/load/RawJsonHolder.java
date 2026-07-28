package net.xolt.freecam.config.load;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public interface RawJsonHolder {
    void setRawJson(JsonObject rawJson);
    @Nullable JsonObject getRawJson();
}
