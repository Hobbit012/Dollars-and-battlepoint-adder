package com.tonpseudo.command;

import com.tonpseudo.economy.Currency;
import com.tonpseudo.economy.EconomyManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class PayCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("pay")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(() -> Text.literal("Utilisation : /pay <joueur> <montant> <monnaie>\nEx : /pay @p 100 dollars"), false);
                    return 1;
                })
                .then(CommandManager.argument("target", EntityArgumentType.players())
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .then(CommandManager.argument("currency", StringArgumentType.word())
                            .executes(ctx -> {
                                ServerPlayerEntity sender = ctx.getSource().getPlayer();
                                Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "target");
                                int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                String currencyStr = StringArgumentType.getString(ctx, "currency");
                                Currency currency = Currency.fromString(currencyStr);

                                if (currency == null) {
                                    ctx.getSource().sendError(Text.literal("Monnaie invalide : " + currencyStr + ". Utilisez 'dollars' ou 'bp'."));
                                    return 0;
                                }

                                if (!EconomyManager.get().takeBalance(sender.getUuid(), currency, amount * targets.size())) {
                                    ctx.getSource().sendError(Text.literal("Fonds insuffisants pour envoyer " + amount + " " + currency + " à " + targets.size() + " joueur(s)."));
                                    return 0;
                                }

                                for (ServerPlayerEntity target : targets) {
                                    EconomyManager.get().addBalance(target.getUuid(), currency, amount);
                                    target.sendMessage(Text.literal("Tu as reçu " + amount + " " + currency + " de " + sender.getName().getString()), false);
                                }

                                ctx.getSource().sendFeedback(() -> Text.literal("Vous avez envoyé " + amount + " " + currency + " à " + targets.size() + " joueur(s)."), false);
                                return 1;
                            })
                        )
                    )
                )
            );
        });
    }
}
