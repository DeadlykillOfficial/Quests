/*******************************************************************************************************
 * Continued by PikaMug (formerly HappyPikachu) with permission from _Blackvein_. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package me.blackvein.quests.convo.conditions.main;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import me.blackvein.quests.Stage;
import me.blackvein.quests.conditions.Condition;
import me.blackvein.quests.convo.conditions.ConditionsEditorNumericPrompt;
import me.blackvein.quests.convo.conditions.menu.ConditionMenuPrompt;
import me.blackvein.quests.convo.conditions.tasks.PlayerPrompt;
import me.blackvein.quests.events.editor.conditions.ConditionsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.Lang;

public class ConditionMainPrompt extends ConditionsEditorNumericPrompt {
    
    private final Quests plugin;
    
    public ConditionMainPrompt(ConversationContext context) {
        super(context);
        this.plugin = (Quests)context.getPlugin();
    }

    private final int size = 5;
    
    public int getSize() {
        return size;
    }
    
    public String getTitle(ConversationContext context) {
        return Lang.get("condition") + ": " + context.getSessionData(CK.C_NAME);
    }
    
    public ChatColor getNumberColor(ConversationContext context, int number) {
        switch (number) {
        case 1:
        case 2:
        case 3:
            return ChatColor.BLUE;
        case 4:
            return ChatColor.GREEN;
        case 5:
            return ChatColor.RED;
        default:
            return null;
        }
    }
    
    public String getSelectionText(ConversationContext context, int number) {
        switch (number) {
        case 1:
            return ChatColor.YELLOW + Lang.get("conditionEditorSetName");
        case 2:
            return ChatColor.GOLD + Lang.get("conditionEditorPlayer");
        case 3:
            return ChatColor.YELLOW + Lang.get("conditionEditorFailQuest") + ":";
        case 4:
            return ChatColor.GREEN + Lang.get("save");
        case 5:
            return ChatColor.RED + Lang.get("exit");
        default:
            return null;
        }
    }
    
    public String getAdditionalText(ConversationContext context, int number) {
        switch (number) {
        case 1:
        case 2:
            return "";
        case 3:
            if (context.getSessionData(CK.C_FAIL_QUEST) == null) {
                context.setSessionData(CK.C_FAIL_QUEST, Lang.get("noWord"));
            }
            return "" + ChatColor.AQUA + context.getSessionData(CK.C_FAIL_QUEST);
        case 4:
        case 5:
            return "";
        default:
            return null;
        }
    }

    @Override
    public String getPromptText(ConversationContext context) {
        ConditionsEditorPostOpenNumericPromptEvent event = new ConditionsEditorPostOpenNumericPromptEvent(context, this);
        plugin.getServer().getPluginManager().callEvent(event);
        
        String text = ChatColor.GOLD + "- " + getTitle(context).replaceFirst(": ", ": " + ChatColor.AQUA) 
                + ChatColor.GOLD + " -\n";
        for (int i = 1; i <= size; i++) {
            text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - " 
                    + getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
        }
        return text;
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, Number input) {
        switch (input.intValue()) {
        case 1:
            return new ConditionNamePrompt();
        case 2:
            return new PlayerPrompt(context);
        case 3:
            String s = (String) context.getSessionData(CK.C_FAIL_QUEST);
            if (s.equalsIgnoreCase(Lang.get("yesWord"))) {
                context.setSessionData(CK.C_FAIL_QUEST, Lang.get("noWord"));
            } else {
                context.setSessionData(CK.C_FAIL_QUEST, Lang.get("yesWord"));
            }
            return new ConditionMainPrompt(context);
        case 4:
            if (context.getSessionData(CK.C_OLD_CONDITION) != null) {
                return new ConditionSavePrompt((String) context.getSessionData(CK.C_OLD_CONDITION));
            } else {
                return new ConditionSavePrompt(null);
            }
        case 5:
            return new ConditionExitPrompt();
        default:
            return new ConditionMainPrompt(context);
        }
    }
    
    private class ConditionNamePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + Lang.get("conditionEditorEnterName");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
                for (Condition c : plugin.getConditions()) {
                    if (c.getName().equalsIgnoreCase(input)) {
                        context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("conditionEditorExists"));
                        return new ConditionNamePrompt();
                    }
                }
                List<String> actionNames = plugin.getConditionFactory().getNamesOfConditionsBeingEdited();
                if (actionNames.contains(input)) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorBeingEdited"));
                    return new ConditionNamePrompt();
                }
                if (input.contains(",")) {
                    context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("questEditorInvalidQuestName"));
                    return new ConditionNamePrompt();
                }
                actionNames.remove((String) context.getSessionData(CK.C_NAME));
                context.setSessionData(CK.C_NAME, input);
                actionNames.add(input);
                plugin.getConditionFactory().setNamesOfConditionsBeingEdited(actionNames);
            }
            return new ConditionMainPrompt(context);
        }
    }

    private class ConditionSavePrompt extends StringPrompt {

        String modName = null;
        LinkedList<String> modified = new LinkedList<String>();

        public ConditionSavePrompt(String modifiedName) {
            if (modifiedName != null) {
                modName = modifiedName;
                for (Quest q : plugin.getQuests()) {
                    for (Stage s : q.getStages()) {
                        if (s.getCondition() != null && s.getCondition().getName() != null) {
                            if (s.getCondition().getName().equalsIgnoreCase(modifiedName)) {
                                modified.add(q.getName());
                                break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String getPromptText(ConversationContext context) {
            String text = ChatColor.YELLOW + Lang.get("questEditorSave") + " \"" + ChatColor.AQUA 
                    + context.getSessionData(CK.C_NAME) + ChatColor.YELLOW + "\"?\n";
            if (modified.isEmpty() == false) {
                text += ChatColor.RED + Lang.get("conditionEditorModifiedNote") + "\n";
                for (String s : modified) {
                    text += ChatColor.GRAY + "    - " + ChatColor.DARK_RED + s + "\n";
                }
                text += ChatColor.RED + Lang.get("conditionEditorForcedToQuit") + "\n";
            }
            return text + ChatColor.GREEN + "1 - " + Lang.get("yesWord") + "\n" + "2 - " + Lang.get("noWord");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase(Lang.get("yesWord"))) {
                plugin.getConditionFactory().saveCondition(context);
                return new ConditionMenuPrompt(context);
            } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase(Lang.get("noWord"))) {
                return new ConditionMainPrompt(context);
            } else {
                return new ConditionSavePrompt(modName);
            }
        }
    }
    
    private class ConditionExitPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String text = ChatColor.GREEN + "" +  ChatColor.BOLD + "1" + ChatColor.RESET + ChatColor.GREEN + " - " 
                    + Lang.get("yesWord") + "\n" + ChatColor.RED + "" +  ChatColor.BOLD + "2" + ChatColor.RESET 
                    + ChatColor.RED + " - " + Lang.get("noWord");
            return ChatColor.YELLOW + Lang.get("confirmDelete") + "\n" + text;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase(Lang.get("yesWord"))) {
                context.getForWhom().sendRawMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + Lang.get("exited"));
                plugin.getConditionFactory().clearData(context);
                return Prompt.END_OF_CONVERSATION;
            } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase(Lang.get("noWord"))) {
                return new ConditionMainPrompt(context);
            } else {
                return new ConditionExitPrompt();
            }
        }
    }
}
