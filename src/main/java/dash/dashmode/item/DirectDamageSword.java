package dash.dashmode.item;

import dash.dashmode.utils.CustomDamageSource;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DirectDamageSword extends SwordItem {
    public static final DamageSource ABSOLUTE_BYPASS = new CustomDamageSource("paper", true, true, false, false, false, false, false);
    private final int attackDamage;

    public DirectDamageSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, 0, attackSpeed, settings);
        this.attackDamage = attackDamage;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean postHit = super.postHit(stack, target, attacker);

        if (postHit) {
            if (!target.damage(ABSOLUTE_BYPASS, getAttackDamage())) {
                target.setHealth(target.getHealth() - getAttackDamage());
            }
        }

        return postHit;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("random_content.any_armor_pass"));
    }
}
