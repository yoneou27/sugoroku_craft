package yoneyone.yone.yo.sugoroku_craft;

import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sugoroku_craft extends JavaPlugin {

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
        if (sender instanceof BlockCommandSender) {
            BlockCommandSender blockCommandSender = (BlockCommandSender) sender;
            BlockState blockState = blockCommandSender.getBlock().getState();
            CommandBlock commandBlock = (CommandBlock) blockState;
            if (label.equals("test")){
                commandBlock.setCommand("テストは成功しました");
                commandBlock.update();
            }
        }else {
            sender.sendMessage("このコマンドはコマンドブロックから実行してください");
        }
        return true;
    }
}
