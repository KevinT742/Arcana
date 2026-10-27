package arcana.mc;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import genesis.api.magic.MagicConcentration;
import genesis.mc.GenesisApi;

/**
 * A lens that lets you see the magic of the ground.
 *
 * <p>It says nothing that the altar does not say, but it says it anywhere: the magic of a region
 * can be looked at without standing next to an altar.
 */
public class ArcaneLens extends Item {

    public ArcaneLens(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel) level;
        Optional<MagicConcentration> concentration = GenesisApi.concentration(server, player.getX(), player.getZ());
        if (concentration.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcana.message.not_genesis"));
            return InteractionResult.FAIL;
        }
        MagicConcentration magic = concentration.get();
        player.sendSystemMessage(Component.translatable("arcana.message.concentration",
                String.format("%.3f", magic.value()), magic.tier()));
        return InteractionResult.SUCCESS;
    }
}
