package dash.dashmode.utils;

import com.google.gson.*;
import net.minecraft.nbt.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NbtUtil {
    public static final String CompoundTagName = "compound";
    public static final Gson GSON = new GsonBuilder().create();

    /**
     * Parse tag from json
     *
     * @param object
     * @return
     */
    public static CompoundTag parseCompound(JsonObject object) {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {

            if (entry.getValue().isJsonObject())
                tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
        }

        return tag;
    }

    private static Tag parseTag(JsonObject element) {
        byte type = element.get("nbt_type").getAsByte();
        JsonElement value = element.get("nbt_value");

        switch (type) {
            case 0:
                return EndTag.INSTANCE;
            case 1:
                return ByteTag.of(value.getAsByte());
            case 2:
                return ShortTag.of(value.getAsShort());
            case 3:
                return IntTag.of(value.getAsInt());
            case 4:
                return LongTag.of(value.getAsLong());
            case 5:
                return FloatTag.of(value.getAsFloat());
            case 6:
                return DoubleTag.of(value.getAsDouble());
            case 7:
                List<Byte> bytes = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsByte)
                        .collect(Collectors.toList());
                return new ByteArrayTag(bytes);
            case 8:
                return StringTag.of(value.toString());

            case 9:
                ListTag listTag = new ListTag();

                JsonArray array = element.getAsJsonArray();
                array.forEach(x -> {
                    if (x.isJsonObject()) {
                        listTag.add(parseTag(x.getAsJsonObject()));
                    }
                });

                return listTag;

            case 10:
                CompoundTag tag = new CompoundTag();

                for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {

                    if (entry.getValue().isJsonObject())
                        tag.put(entry.getKey(), parseTag(entry.getValue().getAsJsonObject()));
                }

                return tag;

            case 11:
                List<Integer> ints = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsInt)
                        .collect(Collectors.toList());
                return new IntArrayTag(ints);
            case 12:
                List<Long> longs = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsLong)
                        .collect(Collectors.toList());
                return new LongArrayTag(longs);
        }


        throw new IllegalStateException("Unknown tag type: " + type);
    }
}
