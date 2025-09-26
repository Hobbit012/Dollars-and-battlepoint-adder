package com.tonpseudo.command;

import com.tonpseudo.economy.Currency;
import com.tonpseudo.economy.EconomyManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class BalanceCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("bal")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(() -> Text.literal("Utilisation : /bal <joueur> <monnaie>\nEx : /bal @s dollars"), false);
                    return 1;
                })
                .then(CommandManager.argument("player", EntityArgumentType.players())
                    .then(CommandManager.argument("currency", StringArgumentType.word())
                        .executes(ctx -> {
                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player");
                            String currencyStr = StringArgumentType.getString(ctx, "currency");
                            Currency currency = Currency.fromString(currencyStr);

                            if (currency == null) {
                                ctx.getSource().sendError(Text.literal("Monnaie invalide : " + currencyStr + ". Utilisez 'dollars' ou 'bp'."));
                                return 0;
                            }

                            for (ServerPlayerEntity player : players) {
                                long amount = EconomyManager.get().getBalance(player.getUuid(), currency);
                                ctx.getSource().sendFeedback(() -> Text.literal("Solde de " + player.getName().getString() + " en " + currency + " : " + amount), false);
                            }

                            return 1;
                        })
                    )
                )
            );
        });
    }
}
