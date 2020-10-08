package dash.dashmode.recipe;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Predicate;

public interface ISerialize extends Predicate<ItemStack> {
    void fromJson(JsonElement object);

    JsonElement toJson();

    void fromBuf(PacketByteBuf buf);

    void toBuf(PacketByteBuf buf);
}
