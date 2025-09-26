package com.tonpseudo.command;

import com.tonpseudo.economy.Currency;
import com.tonpseudo.economy.EconomyManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class EcoAdminCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("eco")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(() -> Text.literal("Utilisation : /eco <give|set|take> <joueur> <montant> <monnaie>\nEx : /eco give @a 100 bp"), false);
                    return 1;
                })
                .then(CommandManager.literal("give")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                            .then(CommandManager.argument("currency", StringArgumentType.word())
                                .executes(ctx -> {
                                    return handleEconomyCommand(ctx.getSource(), "give",
                                            EntityArgumentType.getPlayers(ctx, "player"),
                                            IntegerArgumentType.getInteger(ctx, "amount"),
                                            StringArgumentType.getString(ctx, "currency"));
                                })))))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                            .then(CommandManager.argument("currency", StringArgumentType.word())
                                .executes(ctx -> {
                                    return handleEconomyCommand(ctx.getSource(), "set",
                                            EntityArgumentType.getPlayers(ctx, "player"),
                                            IntegerArgumentType.getInteger(ctx, "amount"),
                                            StringArgumentType.getString(ctx, "currency"));
                                })))))
                .then(CommandManager.literal("take")
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                            .then(CommandManager.argument("currency", StringArgumentType.word())
                                .executes(ctx -> {
                                    return handleEconomyCommand(ctx.getSource(), "take",
                                            EntityArgumentType.getPlayers(ctx, "player"),
                                            IntegerArgumentType.getInteger(ctx, "amount"),
                                            StringArgumentType.getString(ctx, "currency"));
                                })))))
            );
        });
    }

    private static int handleEconomyCommand(ServerCommandSource source, String action,
                                            Collection<ServerPlayerEntity> targets,
                                            int amount, String currencyStr) {

        Currency currency = Currency.fromString(currencyStr);
        if (currency == null) {
            source.sendError(Text.literal("Monnaie invalide : " + currencyStr + ". Utilisez 'dollars' ou 'bp'."));
            return 0;
        }

        for (ServerPlayerEntity target : targets) {
            switch (action) {
                case "give" -> EconomyManager.get().addBalance(target.getUuid(), currency, amount);
                case "set" -> EconomyManager.get().setBalance(target.getUuid(), currency, amount);
                case "take" -> {
                    boolean ok = EconomyManager.get().takeBalance(target.getUuid(), currency, amount);
                    if (!ok) {
                        source.sendError(Text.literal("Impossible de retirer " + amount + " " + currency + " de " + target.getName().getString()));
                        continue;
                    }
                }
            }
        }

        source.sendFeedback(() -> Text.literal("Commande exécutée : " + action + " " + amount + " " + currency + " à " + targets.size() + " joueur(s)."), false);
        return 1;
    }
}
