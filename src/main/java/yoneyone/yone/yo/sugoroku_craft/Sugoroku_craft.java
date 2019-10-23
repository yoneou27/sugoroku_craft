package yoneyone.yone.yo.sugoroku_craft;

import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class Sugoroku_craft extends JavaPlugin implements Listener {
    private final String ln = System.getProperty("line.separator");

    private Map<UUID,Integer> dice_result = new HashMap<>();//サイコロ結果
    private Map<UUID,Boolean> dice_is_finish = new HashMap<>();
    private Map<UUID,Integer> sugoroku_masu = new HashMap<>();//現在のマス
    private Map<UUID,String> sugoroku_place = new HashMap<>();//現在いるすごろく場

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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label) {
            case "kgo":
                try {
                    if (sender.getServer().getPlayer(args[0]) != null) {
                        Player player = sender.getServer().getPlayer(args[0]);
                        if (!sugoroku_place.containsKey(player.getUniqueId())){
                            sender.sendMessage("すごろくを開始していません");
                            return true;
                        }
                        player.sendMessage(args[1] +"進みました");
                        if (go(player, Integer.parseInt(args[1]))) {
                            player.sendMessage("ゴールしました");
                            sugoroku_masu.remove(player.getUniqueId());
                            sugoroku_place.remove(player.getUniqueId());
                        }
                    }else {
                        sender.sendMessage("そのプレイヤーは存在しません");
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    sender.sendMessage("コマンドの使い方を間違えています。\n/kgo [プレイヤー] [進ませたいマスの数（半角）（マイナスも可）]");
                }
                break;
            case "start":
                if (sender instanceof BlockCommandSender) {
                    try {
                        Player player = sender.getServer().getPlayer(args[0]);
                        String d_path = "すごろくデータ\\" + args[0];
                        File file = new File(d_path);
                        if (!file.exists()){
                            player.sendMessage("そのすごろく場は存在しません");
                            return true;
                        }
                        String place = args[1];
                        sugoroku_masu.put(player.getUniqueId(),0);
                        sugoroku_place.put(player.getUniqueId(),place);
                        player.sendMessage("すごろく場"+ place +"でスタートしました");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return true;
                    }
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
                        player.sendMessage("コマンドの使い方を間違えています。\n正しい使い方/masu [すごろくの名前]");
                        return true;
                    }
                } else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
            case "masugoal":
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    try {
                        String d_path = "すごろくデータ\\" + args[0];
                        File file = new File(d_path);
                        if (!file.exists()){
                            player.sendMessage("そのすごろく場は存在しません");
                            return true;
                        }
                        String file_path = "すごろくデータ\\" + args[0] + "\\goal";
                        try (FileWriter fw = new FileWriter(file_path)) {
                            String x = String.valueOf(player.getLocation().getX());
                            String y = String.valueOf(player.getLocation().getY());
                            String z = String.valueOf(player.getLocation().getZ());
                            fw.write(x);
                            fw.write(ln);
                            fw.write(y);
                            fw.write(ln);
                            fw.write(z);
                            player.sendMessage("ゴールを保存しました");
                        } catch (IOException e) {
                            player.sendMessage("エラーが発生しました");
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        player.sendMessage("コマンドの使い方が間違えています。\n正しい使い方/masugoal [すごろくの名前]");
                    }
                }else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
            case "sgo":
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    if (!sugoroku_place.containsKey(player.getUniqueId())){
                        player.sendMessage("すごろくを開始していません");
                        return true;
                    }
                    if (dice_is_finish.get(player.getUniqueId()) != null){
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
                    if (!sugoroku_place.containsKey(player.getUniqueId())){
                        player.sendMessage("すごろくを開始していません");
                        return true;
                    }
                    if (dice_is_finish.get(player.getUniqueId()) != null) {
                        int dice = dice_result.get(player.getUniqueId());
                        player.sendMessage(dice + "進みます");
                        if (go(player, dice)){
                            player.sendMessage("ゴールしました");
                            sugoroku_masu.remove(player.getUniqueId());
                            sugoroku_place.remove(player.getUniqueId());
                        }
                        dice_result.remove(player.getUniqueId());
                        dice_is_finish.remove(player.getUniqueId());
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
            dice_result.put(player.getUniqueId(),result_int);
            dice_is_finish.put(player.getUniqueId(),true);
        }
    }
    private boolean go(Player player,int masu){
        boolean tof = false;
        int now_masu = sugoroku_masu.get(player.getUniqueId());
        int next_masu = now_masu + masu;
        if (next_masu < 0){
            next_masu = 0;
        }
        String file_path = "すごろくデータ\\" + sugoroku_place.get(player.getUniqueId()) + "\\" + next_masu;
        File file = new File(file_path);
        if (!file.exists()){
            file_path = "すごろくデータ\\" + sugoroku_place.get(player.getUniqueId()) + "\\goal";
            tof = true;
        }
        try (FileReader fr = new FileReader(file_path)){
            BufferedReader br = new BufferedReader(fr);
            String x = br.readLine();
            String y = br.readLine();
            String z = br.readLine();
            double mx = Double.parseDouble(x);
            double my = Double.parseDouble(y);
            double mz = Double.parseDouble(z);
            float yaw = player.getLocation().getYaw();
            float pitch = player.getLocation().getPitch();
            player.teleport(new Location(player.getWorld(),mx,my,mz,yaw,pitch));
            sugoroku_masu.put(player.getUniqueId(),next_masu);
        }catch (IOException e){
            player.sendMessage("エラーが発生しました");
        }
        return tof;
    }
}
