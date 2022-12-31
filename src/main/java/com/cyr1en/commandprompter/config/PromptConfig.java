package com.cyr1en.commandprompter.config;

import com.cyr1en.commandprompter.config.annotations.field.ConfigNode;
import com.cyr1en.commandprompter.config.annotations.field.NodeComment;
import com.cyr1en.commandprompter.config.annotations.field.NodeDefault;
import com.cyr1en.commandprompter.config.annotations.field.NodeName;
import com.cyr1en.commandprompter.config.annotations.type.ConfigHeader;
import com.cyr1en.commandprompter.config.annotations.type.ConfigPath;
import com.cyr1en.commandprompter.config.annotations.type.Configuration;
import com.cyr1en.kiso.mc.configuration.base.Config;

@Configuration
@ConfigPath("prompt-config.yml")
@ConfigHeader({"Prompts", "Configuration"})
public class PromptConfig {
        Config rawConfig;

        @ConfigNode
        @NodeName("PlayerUI.Skull-Name-Format")
        @NodeDefault("&6%s")
        @NodeComment({
                "选择玩家 UI 的样式设置", "",
                "Skull-Name-Format - 玩家头颅物品显示名称", "",
                "Size - UI 的大小 (应是 9 的倍数, 在 18-54 之间)", "",
                "Sorted - 是否需要对玩家头颅进行排序?", "",
                "Per-World - 是否只显示当前世界的玩家?"
        })
        public
        String skullNameFormat;

        @ConfigNode
        @NodeName("PlayerUI.Size")
        @NodeDefault("54")
        public
        int playerUISize;

        @ConfigNode
        @NodeName("PlayerUI.Cache-Size")
        @NodeDefault("256")
        public
        int cacheSize;

        @ConfigNode
        @NodeName("PlayerUI.Previous.Item")
        @NodeDefault("Feather")
        public
        String previousItem;

        @ConfigNode
        @NodeName("PlayerUI.Previous.Column")
        @NodeDefault("3")
        public
        int previousColumn;

        @ConfigNode
        @NodeName("PlayerUI.Previous.Text")
        @NodeDefault("&7◀◀ 上一页")
        public
        String previousText;

        @ConfigNode
        @NodeName("PlayerUI.Next.Item")
        @NodeDefault("Feather")
        public
        String nextItem;

        @ConfigNode
        @NodeName("PlayerUI.Next.Column")
        @NodeDefault("7")
        public
        int nextColumn;

        @ConfigNode
        @NodeName("PlayerUI.Next.Text")
        @NodeDefault("下一页 ▶▶")
        public
        String nextText;

        @ConfigNode
        @NodeName("PlayerUI.Cancel.Item")
        @NodeDefault("Barrier")
        public
        String cancelItem;

        @ConfigNode
        @NodeName("PlayerUI.Cancel.Column")
        @NodeDefault("5")
        public
        int cancelColumn;

        @ConfigNode
        @NodeName("PlayerUI.Cancel.Text")
        @NodeDefault("&7取消 ✘")
        public
        String cancelText;

        @ConfigNode
        @NodeName("PlayerUI.Sorted")
        @NodeDefault("false")
        public
        boolean sorted;

        @ConfigNode
        @NodeName("PlayerUI.Per-World")
        @NodeDefault("false")
        public
        boolean isPerWorld;

        @ConfigNode
        @NodeName("AnvilGUI.Enable-Title")
        @NodeDefault("true")
        @NodeComment({
                "铁砧 UI 的样式设置", "",
                "Enable-Title - 是否在补全的第一行显示铁砧 UI 的标题", "",
                "Item - 放在铁砧 UI 上的物品 ID", "",
                "Enchanted - 是否需要物品发出附魔光泽?", "",
                "Custom-Title - 如果开启了标题，且补全时没有设置自定义标题，则会使用该默认标题"
        })
        public
        boolean enableTitle;

        @ConfigNode
        @NodeName("AnvilGUI.Custom-Title")
        @NodeDefault("")
        public
        String customTitle;

        @ConfigNode
        @NodeName("AnvilGUI.Item")
        @NodeDefault("Paper")
        public
        String anvilItem;

        @ConfigNode
        @NodeName("AnvilGUI.Enchanted")
        @NodeDefault("false")
        public
        boolean anvilEnchanted;

        @ConfigNode
        @NodeName("TextPrompt.Clickable-Cancel")
        @NodeDefault("true")
        @NodeComment({
                "文本补全设置", "",
                "Clickable-Cancel - 是否在聊天栏显示可点击的取消链接", "",
                "Cancel-Message - 取消链接的文本", "",
                "Cancel-Hover-Message - 取消链接的鼠标悬停信息", "",
                "Response-Listener-Priority - 监听玩家回复的事件优先级",
                "(可用的优先级有 DEFAULT, LOW, LOWEST, NORMAL, HIGH, HIGHEST)",
                "(除非有特殊需要, 一般不需要修改优先级)"
        })
        public
        boolean sendCancelText;

        @ConfigNode
        @NodeName("TextPrompt.Cancel-Message")
        @NodeDefault("&7[&c&l✘点击取消&7]")
        public
        String textCancelMessage;

        @ConfigNode
        @NodeName("TextPrompt.Cancel-Hover-Message")
        @NodeDefault("&7点击取消该命令补全操作")
        public
        String textCancelHoverMessage;

        @ConfigNode
        @NodeName("TextPrompt.Response-Listener-Priority")
        @NodeDefault("DEFAULT")
        public
        String responseListenerPriority;

        @ConfigNode
        @NodeName("SignUI.Input-Field-Location")
        @NodeDefault("bottom")
        @NodeComment({
                "牌子 UI 设置",
                "",
                "Input-Field-Location - 需要玩家输入补全内容的位置 (功能未完成)",
                "",
                "可用的位置如下",
                "top - 第1行填写参数",
                "top-aggregate - 第4行为提示，前3行都是要填写的参数",
                "bottom - 第4行填写参数.",
                "bottom-aggregate - 第1行为提示，后3行都是要填写的参数",
                "",
                "查看 Wiki 寻找牌子 UI 的额外设置:",
                "https://github.com/CyR1en/CommandPrompter/wiki/Prompts#signui-prompt"
        })
        String inputFieldLocation;
}
