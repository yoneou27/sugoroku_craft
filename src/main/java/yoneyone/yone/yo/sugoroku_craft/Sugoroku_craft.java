package yoneyone.yone.yo.sugoroku_craft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class Sugoroku_craft extends JavaPlugin implements Listener {
    private final String ln = System.getProperty("line.separator");

    private Map<Player,Integer> dice_result = new HashMap<>();//

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.equals(ChatColor.translateAlternateColorCodes('&',"&lサイコロ結果"))){
            e.setCancelled(true);
        }
    }
    @EventHandler//ログアウト検知
    public void PlayerQuitEvent(PlayerQuitEvent e){

    }
    @EventHandler//インベントリを閉じたかどうか検知
    public  void InventoryCloseEvent(InventoryCloseEvent e){

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label) {
            case "test":
                if (sender instanceof BlockCommandSender) {
                    BlockCommandSender blockCommandSender = (BlockCommandSender) sender;
                    BlockState blockState = blockCommandSender.getBlock().getState();
                    CommandBlock commandBlock = (CommandBlock) blockState;
                    commandBlock.setCommand("テストは成功しました");
                    commandBlock.update();
                } else {
                    sender.sendMessage("このコマンドはコマンドブロックから実行してください");
                }
                break;
            case "masu":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    try {
                        String d_path = "すごろくデータ\\" + args[0];
                        if ((new File("すごろくデータ")).mkdir()) {
                            System.out.println("すごろくデータディレクトリを作成しました");
                        }
                        if (new File(d_path).mkdir()) {
                            player.sendMessage("新しいすごろく場を生成しました");
                        }
                        String[] file_list = new File(d_path).list();
                        int i = -1;
                        if (file_list != null) {
                            for (String str : file_list) {
                                int l = Integer.parseInt(str);
                                if (l > i) {
                                    i = l;
                                }
                            }
                        }
                        i += 1;
                        String file_path = "すごろくデータ\\" + args[0] + "\\" + i;
                        try (FileWriter fw = new FileWriter(file_path)) {
                            String x = String.valueOf(player.getLocation().getX());
                            String y = String.valueOf(player.getLocation().getY());
                            String z = String.valueOf(player.getLocation().getZ());
                            fw.write(x);
                            fw.write(ln);
                            fw.write(y);
                            fw.write(ln);
                            fw.write(z);
                            player.sendMessage("マス「" + i + "」を保存しました");
                        } catch (IOException e) {
                            player.sendMessage("ファイル書きこみエラーが発生しました");
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        player.sendMessage("コマンドの使い方を間違えています。" + ln + "正しい使い方/masu [すごろくの名前]");
                        return true;
                    }
                } else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
            case "sgo":
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    if (dice_result.containsKey(player)){
                        player.sendMessage("既にサイコロを振っています");
                    }else {
                        dice_GUI(player, 100);
                    }
                } else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
            case "mgo":
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    if (dice_result.containsKey(player)){
                        int dice = dice_result.get(player);
                        player.sendMessage(dice + "進みます");
                        //ここに進む処理
                    }else {
                        player.sendMessage("結果ががありません\nサイコロを振っていない可能性があります");
                    }
                } else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
        }
        return true;
    }
    private void dice_GUI(Player player,int time){
        String title = ChatColor.translateAlternateColorCodes('&',"&lサイコロ結果");
        Inventory inv = Bukkit.createInventory(null, 9,title);
        ItemStack dice = new ItemStack(Material.APPLE);
        ItemMeta meta_result = dice.getItemMeta();

        int result_int = new Random().nextInt(6) + 1;
        String message_result = ChatColor.translateAlternateColorCodes('&',"結果："+ result_int);
        meta_result.setDisplayName(message_result);
        dice.setItemMeta(meta_result);

        inv.setItem(4,dice);

        player.openInventory(inv);

        player.playEffect(player.getLocation(), Effect.IRON_DOOR_CLOSE,null);
        int i;
        if (time > 40){
            i = 40;
        }else if (time > 15){
            i = 85;
        }else if (time > 5){
            i = 210;
        }else {
            i = 700;
        }
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        time -= 1;
        if (time > 0){
            dice_GUI(player,time);
        }else {
            dice_result.put(player,result_int);
        }
    }
}
