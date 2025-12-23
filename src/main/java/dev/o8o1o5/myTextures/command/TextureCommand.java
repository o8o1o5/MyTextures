package dev.o8o1o5.myTextures.command;

import dev.o8o1o5.myTextures.MyTextures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TextureCommand implements CommandExecutor, TabCompleter {

    private final MyTextures plugin;

    public TextureCommand(MyTextures plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "register":
                handleRegister(player, args);
                break;
            case "name":
                handleName(player, args);
                break;
            case "give":
                handleGive(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleRegister(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /mt register <id> [item_type]");
            return;
        }

        String id = args[1];
        Material mat = Material.PAPER;

        if (args.length >= 3) {
            Material inputMat = Material.getMaterial(args[2].toUpperCase());
            if (inputMat == null) {
                player.sendMessage(ChatColor.RED + "올바른 아이템 타입이 아닙니다.");
                return;
            }
            mat = inputMat;
        }

        if (plugin.getItemRegistry().exists(id)) {
            player.sendMessage(ChatColor.RED + "이미 등록된 아이템 ID 입니다.");
            return;
        }

        if (plugin.getFileManager().generateResourceFiles(id)) {
            plugin.getItemRegistry().registerItemData(id, mat);
            plugin.getItemRegistry().saveItems();
            player.sendMessage(id + " 아이템이 성공적으로 등록되었습니다.");
            player.sendMessage(ChatColor.DARK_GRAY + "(리소스팩 새로고침: F3 + T)");
        } else {
            player.sendMessage(ChatColor.RED + "images 폴더에 " + id + ".png 파일이 있는지 확인해주세요.");
        }
    }

    private void handleName(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /mt name <id> <name>");
            return;
        }

        String id = args[1];

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newName = sb.toString().trim().replace("&", "§");

        if (plugin.getItemRegistry().updateDisplayName(id, newName)) {
            plugin.getItemRegistry().saveItems();
            player.sendMessage(ChatColor.GREEN + id + "의 이름이 변경되었습니다: " + newName);
        } else {
            player.sendMessage(ChatColor.RED + "등록되지 않은 아이템 ID 입니다.");
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "사용법: /mt give <대상> <id> [수량]");
            return;
        }

        String selector = args[1];
        String id = args[2];
        int amount = (args.length >= 4) ? parseAmount(args[3]) : 1;

        try {
            List<Entity> targets = Bukkit.selectEntities(sender, selector);
            ItemStack item = plugin.getItemRegistry().createItem(id);

            if (item == null) {
                sender.sendMessage(ChatColor.RED + "등록되지 않은 아이템 ID 입니다.");
                return;
            }
            item.setAmount(amount);

            int count = 0;
            for (Entity entity : targets) {
                if (entity instanceof Player targetPlayer) {
                    targetPlayer.getInventory().addItem(item.clone());
                    count++;
                }
            }
            sender.sendMessage(ChatColor.GREEN + "" + count + "명의 대상에게 " + id + " 아이템을 " + amount + "개씩 지급했습니다.");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "올바르지 않은 대상 선택자입니다.");
        }
    }

    private int parseAmount(String input) {
        try {
            return Math.max(1, Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "사용법: /mt remove <id>");
            return;
        }

        String id = args[1];
        if (plugin.getItemRegistry().removeItem(id)) {
            player.sendMessage(ChatColor.GREEN + "아이템이 성공적으로 삭제되었습니다.");
        } else {
            player.sendMessage(ChatColor.RED + "등록되지 않은 ID 입니다.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("[ MyTextures Command Help ]");
        player.sendMessage("/mt register <id> [type] " + ChatColor.DARK_GRAY + "아이템을 등록합니다.");
        player.sendMessage("/mt name <id> <Name> " + ChatColor.DARK_GRAY + "아이템의 표시 이름을 설정합니다.");
        player.sendMessage("/mt give <id> " + ChatColor.DARK_GRAY + "아이템을 지급합니다.");
        player.sendMessage("/mt remove <id> " + ChatColor.DARK_GRAY + "아이템의 등록을 취소합니다.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("register", "name", "give", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        switch (args[0].toLowerCase()) {
            case "register":
                if (args.length == 2) {
                    // TODO: 이미지는 존재하나 아직 등록되지 않은 아이디를 추천해주면 좋겠습니다만
                    //       일단은 빈 리스트를 반환하여 사용자가 ID 를 직접 치도록 유도합니다
                    return Collections.emptyList();
                }
                if (args.length == 3) {
                    return Arrays.stream(Material.values())
                            .map(m -> m.name().toLowerCase())
                            .filter(m -> m.startsWith(args[2].toLowerCase()))
                            .limit(15).collect(Collectors.toList());
                }
                break;
            case "name":
            case "give":
                if (args.length == 2 && args[0].equalsIgnoreCase("give")) return null;
                if ((args.length == 2 && args[0].equalsIgnoreCase("name")) || (args.length == 3)) {
                    return plugin.getItemRegistry().getItemList().keySet().stream()
                            .filter(id -> id.startsWith(args[args.length - 1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                break;
            case "remove":
                if (args.length < 2) {
                    return plugin.getItemRegistry().getItemList().keySet().stream()
                            .filter(id -> id.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                break;
        }
        return new ArrayList<>();
    }
}
