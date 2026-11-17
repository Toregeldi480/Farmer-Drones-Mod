package com.farm.item;

import com.farm.init.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class FloppyDisk extends Item {
    public FloppyDisk() {
        this("");
    }

    public FloppyDisk(String source) {
        super(new Item.Properties().component(ModComponents.DISK_SOURCE_CODE, source));
    }

    public FloppyDisk(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.sendSystemMessage(Component.literal("\nDisk Source Code:\n" + player.getItemInHand(hand).getOrDefault(ModComponents.DISK_SOURCE_CODE, "No Data Is Written")));
        return super.use(level, player, hand);
    }
}
