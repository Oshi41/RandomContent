package dash.dashmode.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class TagIngredient implements ISerialize {
    private Tag<Item> tag;

    @Override
    public void fromJson(JsonElement object) {
        if (object.isJsonObject()) {
            Identifier tagId = new Identifier(JsonHelper.getString(object.getAsJsonObject(), "tag"));
            tag = ServerTagManagerHolder.getTagManager().getItems().getTag(tagId);
        }
    }

    @Override
    public JsonElement toJson() {
        Identifier id = ServerTagManagerHolder.getTagManager().getItems().getTagId(tag);
        JsonObject object = new JsonObject();

        object.addProperty("tag", id.toString());
        return object;
    }

    @Override
    public void fromBuf(PacketByteBuf buf) {
        int length = buf.readVarInt();

        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            stacks.add(buf.readItemStack());
        }
    }

    @Override
    public void toBuf(PacketByteBuf buf) {
        buf.writeVarInt(tag.values().size());

        tag.values()
                .stream()
                .map(Item::getDefaultStack)
                .forEach(buf::writeItemStack);
    }

    @Override
    public boolean test(ItemStack stack) {
        return tag.contains(stack.getItem());
    }
}
