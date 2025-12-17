package dev.o8o1o5.myTextures.command;

import dev.o8o1o5.myTextures.MyTextures;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TextureCommand implements CommandExecutor {

    private final MyTextures plugin;

    public TextureCommand(MyTextures plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length < 3 || !args[0].equalsIgnoreCase("register")) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /mt register <아이디> <아이템 타입>");
            return true;
        }

        String id = args[1];
        String materialName = args[2].toUpperCase();
        Material material = Material.getMaterial(materialName);

        if (material == null) {
            player.sendMessage(ChatColor.RED + "존재하지 않는 아이템 타입입니다: " + materialName);
            return true;
        }

        if (plugin.getItemManager().registerItem(id)) {
            ItemStack customItem = plugin.getItemManager().createCustomItem(id, material);
            player.getInventory().addItem(customItem);

            player.sendMessage(id + " 아이템이 지급되었습니다.");
        } else {
            player.sendMessage("");
        }
    }
}
