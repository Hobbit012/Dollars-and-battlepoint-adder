package com.tonpseudo;

import com.tonpseudo.command.BalanceCommand;
import com.tonpseudo.command.PayCommand;
import com.tonpseudo.command.EcoAdminCommand;
import net.fabricmc.api.ModInitializer;

public class EconomyMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BalanceCommand.register();
        PayCommand.register();
        EcoAdminCommand.register();

        // On pourrait sauvegarder régulièrement, par exemple via scheduler, mais pour début on sauvegarde à l’arrêt du serveur
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            com.tonpseudo.economy.EconomyManager.get().saveAll();
        }));
    }
}
