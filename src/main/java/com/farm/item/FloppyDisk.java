package com.farm.item;

import com.farm.init.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

public class FloppyDisk extends Item {
    public FloppyDisk(Properties properties) {
        super(properties);
    }

    @Override
    @NullMarked
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.sendSystemMessage(Component.literal("\nDisk Source Code:\n" + player.getItemInHand(hand).getOrDefault(ModComponents.DISK_SOURCE_CODE, "No Data Is Written")));
        return super.use(level, player, hand);
    }
}
