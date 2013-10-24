package me.blackvein.quests.prompts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.blackvein.quests.ColorUtil;
import me.blackvein.quests.ItemData;
import me.blackvein.quests.ItemData.Data;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackPrompt extends FixedSetPrompt implements ColorUtil{

    //Stores itemstack in "tempStack" context data.
    //Stores id in "tempId"
    //Stores amount in "tempAmount"
    //Stores data in "tempData"
    //Stores enchantments in "tempEnchantments"
    //Stores name in "tempName"
    //Stores lore in "tempLore"

    final Prompt oldPrompt;
    public ItemStackPrompt(Prompt old){

        super("0", "1", "2", "3", "4", "5", "6", "7", "8");
        oldPrompt = old;

    }

    @Override
    public String getPromptText(ConversationContext cc) {
        String menu = YELLOW + "- " + GOLD + "Create Item " + YELLOW + "-\n";
        if(cc.getSessionData("tempId") != null){
            String stackData = getItemData(cc);
            if(stackData != null)
                menu += stackData;
        }else
            menu += "\n";
        menu += GOLD + "" + BOLD + "0. " + RESET + "" + YELLOW + "Load item in hand\n";
        menu += YELLOW + "" + BOLD + "1. " + RESET + "" + GOLD + "Set ID\n";
        menu += YELLOW + "" + BOLD + "2. " + RESET + "" + GOLD + "Set amount\n";
        menu += YELLOW + "" + BOLD + "3. " + RESET + "" + GOLD + "Set data\n";
        menu += YELLOW + "" + BOLD + "4. " + RESET + "" + GOLD + "Add/clear enchantments\n";
        menu += YELLOW + "" + BOLD + "5. " + RESET + "" + ITALIC + GOLD + "Set name\n";
        menu += YELLOW + "" + BOLD + "6. " + RESET + "" + ITALIC + GOLD + "Set lore\n";
        menu += YELLOW + "" + BOLD + "7. " + RESET + "" + GREEN + "Done\n";
        menu += YELLOW + "" + BOLD + "8. " + RESET + "" + RED + "Cancel\n";
        return menu;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String input) {

        if(input.equalsIgnoreCase("0")){

            Player player = (Player) cc.getForWhom();
            ItemStack is = player.getItemInHand();
            if(is == null || is.getType().equals(Material.AIR)){

                player.sendMessage(RED + "No item in hand!");
                return new ItemStackPrompt(oldPrompt);

            }else{

                cc.setSessionData("tempData", null);
                cc.setSessionData("tempEnchantments", null);
                cc.setSessionData("tempName", null);
                cc.setSessionData("tempLore", null);

                cc.setSessionData("tempId", is.getTypeId());
                cc.setSessionData("tempAmount", is.getAmount());
                if(is.getDurability() != 0)
                    cc.setSessionData("tempData", is.getDurability());
                if(is.getEnchantments() != null && is.getEnchantments().isEmpty() == false)
                    cc.setSessionData("tempEnchantments", new HashMap<Enchantment,Integer>(is.getEnchantments()));
                if(is.hasItemMeta()){

                    ItemMeta meta = is.getItemMeta();
                    if(meta.hasDisplayName())
                        cc.setSessionData("tempName", ChatColor.stripColor(meta.getDisplayName()));
                    if(meta.hasLore()){
                        LinkedList<String> lore = new LinkedList<String>();
                        lore.addAll(meta.getLore());
                        cc.setSessionData("tempLore", lore);
                    }


                }

                player.sendMessage(GREEN + "Item loaded.");
                return new ItemStackPrompt(oldPrompt);

            }

        }else if(input.equalsIgnoreCase("1")){
            return new IDPrompt();
        }else if(input.equalsIgnoreCase("2")){

            if(cc.getSessionData("tempId") != null){
                return new AmountPrompt();
            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("3")){

            if(cc.getSessionData("tempId") != null && cc.getSessionData("tempAmount") != null){
                return new DataPrompt();
            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID and amount first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("4")){

            if(cc.getSessionData("tempId") != null && cc.getSessionData("tempAmount") != null){
                return new EnchantmentPrompt();
            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID and amount first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("5")){

            if(cc.getSessionData("tempId") != null && cc.getSessionData("tempAmount") != null){
                return new NamePrompt();
            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID and amount first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("6")){

            if(cc.getSessionData("tempId") != null && cc.getSessionData("tempAmount") != null){
                return new LorePrompt();
            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID and amount first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("7")){

            if(cc.getSessionData("tempId") != null && cc.getSessionData("tempAmount") != null){

                int id = (Integer) cc.getSessionData("tempId");
                int amount = (Integer) cc.getSessionData("tempAmount");
                short data = -1;
                Map<Enchantment, Integer> enchs = null;
                String name = null;
                LinkedList<String> lore = null;

                if(cc.getSessionData("tempData") != null)
                    data = (Short) cc.getSessionData("tempData");
                if(cc.getSessionData("tempEnchantments") != null)
                    enchs = (Map<Enchantment, Integer>) cc.getSessionData("tempEnchantments");
                if(cc.getSessionData("tempName") != null)
                    name = (String) cc.getSessionData("tempName");
                if(cc.getSessionData("tempLore") != null)
                    lore = (LinkedList<String>) cc.getSessionData("tempLore");

                ItemStack stack = new ItemStack(id, amount);
                ItemMeta meta = stack.getItemMeta();

                if(data != -1)
                    stack.setDurability((short)data);
                if(enchs != null){
                    for(Entry<Enchantment, Integer> e : enchs.entrySet())
                        meta.addEnchant(e.getKey(), e.getValue(), true);
                }
                if(name != null)
                    meta.setDisplayName(name);
                if(lore != null)
                    meta.setLore(lore);

                stack.setItemMeta(meta);

                cc.setSessionData("tempStack", stack);
                cc.setSessionData("newItem", Boolean.TRUE);


            }else{
                cc.getForWhom().sendRawMessage(RED + "You must set an ID and amount first!");
                return new ItemStackPrompt(oldPrompt);
            }

        }else if(input.equalsIgnoreCase("8"))
            cc.setSessionData("tempStack", null);

        cc.setSessionData("tempId", null);
        cc.setSessionData("tempAmount", null);
        cc.setSessionData("tempData", null);
        cc.setSessionData("tempEnchantments", null);
        cc.setSessionData("tempName", null);
        cc.setSessionData("tempLore", null);

        try{
            return oldPrompt;
        }catch (Exception e){
            cc.getForWhom().sendRawMessage(RED + "A critical error has occurred.");
            return Prompt.END_OF_CONVERSATION;
        }
    }


    private class IDPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return YELLOW + "Enter an item ID, or \"cancel\" to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {
            if(input.equalsIgnoreCase("cancel") == false){

                try{
                    Material mat = Material.getMaterial(Integer.parseInt(input));
                    if(mat == null){
                        cc.getForWhom().sendRawMessage(RED + "Invalid item ID!");
                        return new IDPrompt();
                    }else{
                        cc.setSessionData("tempId", Integer.parseInt(input));
                        return new ItemStackPrompt(oldPrompt);
                    }

                }catch(NumberFormatException e){
                	try {
	                	Data data = ItemData.getInstance().getItem(input);
	                	if (data == null) {
	                		cc.getForWhom().sendRawMessage(RED + "Invalid item ID!");
	                		return new IDPrompt();
	                	} else {

	                		cc.setSessionData("tempId", data.getId());
	                		cc.setSessionData("tempData", (data.getData() == 0) ? null : (short)data.getData());
	                		return new ItemStackPrompt(oldPrompt);
	                	}
                	} catch (Exception e1) {
                		e1.printStackTrace();
                		cc.getForWhom().sendRawMessage(RED + "Invalid item ID!");
                		return new IDPrompt();
                	}
                }

            }else{

                return new ItemStackPrompt(oldPrompt);

            }
        }



    }

    private class AmountPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return YELLOW + "Enter item amount (max. 64), or \"cancel\" to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {
            if(input.equalsIgnoreCase("cancel") == false){

                try{

                    int amt = Integer.parseInt(input);
                    if(amt < 1 || amt > 64){
                        cc.getForWhom().sendRawMessage(RED + "Amount must be between 1-64!");
                        return new AmountPrompt();
                    }else{
                        cc.setSessionData("tempAmount", Integer.parseInt(input));
                        return new ItemStackPrompt(oldPrompt);
                    }

                }catch(NumberFormatException e){
                    cc.getForWhom().sendRawMessage(RED + "Invalid input!");
                    return new AmountPrompt();
                }

            }else{

                return new ItemStackPrompt(oldPrompt);

            }
        }

    }

    private class DataPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return YELLOW + "Enter item data, or \"clear\" to clear the data, or \"cancel\" to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {
            if(input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false){

                try{

                    int amt = Integer.parseInt(input);
                    if(amt < 1){
                        cc.getForWhom().sendRawMessage(RED + "Amount must be greater than 0! (default data is 0)");
                        return new DataPrompt();
                    }else{
                        cc.setSessionData("tempData", Short.parseShort(input));
                        return new ItemStackPrompt(oldPrompt);
                    }

                }catch(NumberFormatException e){
                    cc.getForWhom().sendRawMessage(RED + "Invalid input!");
                    return new DataPrompt();
                }

            }else if(input.equalsIgnoreCase("clear")){

                cc.setSessionData("tempData", null);

            }

            return new ItemStackPrompt(oldPrompt);

        }

    }

    private class EnchantmentPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {

            String text = PINK + "- " + PURPLE + "Enchantments" + PINK + " -\n";
            for (Enchantment e : Enchantment.values()) {

                text += GREEN + Quester.prettyEnchantmentString(e) + ", ";

            }
            text = text.substring(0, text.length() - 1);


            return text + "\n" + YELLOW + "Enter an enchantment name, or \"clear\" to clear the enchantments, or \"cancel\" to return.";

        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {

            if(input.equalsIgnoreCase("clear") == false && input.equalsIgnoreCase("cancel") == false){

                Enchantment e = Quests.getEnchantmentPretty(input);
                if(e != null){

                    cc.setSessionData("tempEnchant", e);
                    return new LevelPrompt(Quester.prettyEnchantmentString(e));

                }else{

                    cc.getForWhom().sendRawMessage(RED + "Invalid enchantment name!");
                    return new EnchantmentPrompt();

                }

            }else if(input.equalsIgnoreCase("clear"))
                cc.setSessionData("tempEnchantments", null);

            return new ItemStackPrompt(oldPrompt);
        }

        protected class LevelPrompt extends StringPrompt {

            final String enchantment;

            protected LevelPrompt(String ench){
                enchantment = ench;
            }

            @Override
            public String getPromptText(ConversationContext cc) {
                return AQUA + "Enter a level (number) for " + enchantment;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String input) {

                try{

                    int num = Integer.parseInt(input);
                    if(num < 1){
                        cc.getForWhom().sendRawMessage(RED + "Level must be greater than 0!");
                        return new LevelPrompt(enchantment);
                    }else{

                        if(cc.getSessionData("tempEnchantments") != null){

                            Map<Enchantment, Integer> enchs = (Map<Enchantment, Integer>) cc.getSessionData("tempEnchantments");
                            enchs.put((Enchantment)cc.getSessionData("tempEnchant"), num);

                        }else{

                            Map<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
                            enchs.put((Enchantment)cc.getSessionData("tempEnchant"), num);

                        }
                        return new ItemStackPrompt(oldPrompt);
                    }

                }catch (NumberFormatException e){
                    cc.getForWhom().sendRawMessage(RED + "Input was not a number!");
                    e.printStackTrace();
                    return new LevelPrompt(enchantment);
                }

            }



        }

    }

    private class NamePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return YELLOW + "Enter item name, or \"clear\" to clear the custom name, or \"cancel\" to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {
            if(input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false){

            	input = Quests.parseString(input);
            	
                cc.setSessionData("tempName", input);

            }else if(input.equalsIgnoreCase("clear")){

                cc.setSessionData("tempName", null);

            }

            return new ItemStackPrompt(oldPrompt);

        }

    }

    private class LorePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext cc) {
            return YELLOW + "Enter item lore, separating each line by a semi-colon ; or \"clear\" to clear the lore, or \"cancel\" to return.";
        }

        @Override
        public Prompt acceptInput(ConversationContext cc, String input) {
            if(input.equalsIgnoreCase("cancel") == false && input.equalsIgnoreCase("clear") == false){
            	
            	input = Quests.parseString(input);

                LinkedList<String> lore = new LinkedList<String>();
                lore.addAll(Arrays.asList(input.split(";")));
                cc.setSessionData("tempLore", lore);

            }else if(input.equalsIgnoreCase("clear")){

                cc.setSessionData("tempLore", null);

            }

            return new ItemStackPrompt(oldPrompt);

        }

    }




    private String getItemData(ConversationContext cc){

        if(cc.getSessionData("tempId") != null){

            String item;

            if(cc.getSessionData("tempName") == null){

                Integer id = (Integer) cc.getSessionData("tempId");
                item = AQUA + Quester.prettyItemString(id);

                if(cc.getSessionData("tempData") != null)
                    item += ":" + BLUE + (Short) cc.getSessionData("tempData");

            }else {

                item = PINK + "" + ITALIC + (String) cc.getSessionData("tempName") + RESET + "" + GRAY + " (";
                Integer id = (Integer) cc.getSessionData("tempId");
                item += AQUA + Quester.prettyItemString(id);
                if(cc.getSessionData("tempData") != null)
                    item += ":" + BLUE + (Short) cc.getSessionData("tempData");
                item += GRAY + ")";

            }

            if(cc.getSessionData("tempAmount") != null)
                item += GRAY + " x " + DARKAQUA + (Integer) cc.getSessionData("tempAmount");
            else
                item += GRAY + " x " + DARKAQUA + "1";

            item += "\n";

            if(cc.getSessionData("tempEnchantments") != null){

                Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) cc.getSessionData("tempEnchantments");
                for(Entry<Enchantment, Integer> e : enchantments.entrySet()){

                    item += GRAY + "  - " + RED + Quester.prettyEnchantmentString(e.getKey()) + " " + Quests.getNumeral(e.getValue()) + "\n";

                }

            }

            if(cc.getSessionData("tempLore") != null){

                List<String> lore = (List<String>) cc.getSessionData("tempLore");

                item += DARKGREEN + "(Lore)\n\"";
                for(String s : lore){

                    if(lore.indexOf(s) != (lore.size() - 1))
                        item += DARKGREEN + "" + ITALIC + s + "\n";
                    else
                        item += DARKGREEN + "" + ITALIC + s + "\"\n";

                }

            }


            item += "\n";
            return item;

        }else{
            return null;
        }

    }

}
