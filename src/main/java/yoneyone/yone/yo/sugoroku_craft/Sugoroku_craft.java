package yoneyone.yone.yo.sugoroku_craft;

import com.google.gson.internal.$Gson$Preconditions;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public final class Sugoroku_craft extends JavaPlugin {
    private final String ln = System.getProperty("line.separator");
    @Override
    public void onEnable() {
        // Plugin startup logic
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
                    dice_GUI(player);
                } else {
                    sender.sendMessage("このコマンドはプレイヤーから実行してください");
                }
                break;
        }
        return true;
    }
    private void dice_GUI(Player player){
        //ここにサイコロを実装
    }
}
