package com.xminer.virtualplayer.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.xminer.virtualplayer.net.BotActionPacket;
import com.xminer.virtualplayer.net.BotClientState;
import com.xminer.virtualplayer.net.Network;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class BotScreen extends Screen {
    private TextFieldWidget nameField;

    public BotScreen() {
        super(new TranslationTextComponent("gui.virtualplayer.title"));
    }

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int cx = this.width / 2;
        int top = this.height / 2 - 70;

        this.nameField = new TextFieldWidget(this.font, cx - 100, top + 30, 200, 20,
                new TranslationTextComponent("gui.virtualplayer.name"));
        this.nameField.setMaxStringLength(16);
        this.children.add(this.nameField);

        this.addButton(new Button(cx - 100, top + 58, 200, 20,
                new TranslationTextComponent("gui.virtualplayer.spawn"),
                b -> send(BotActionPacket.SPAWN)));
        this.addButton(new Button(cx - 100, top + 82, 98, 20,
                new TranslationTextComponent("gui.virtualplayer.remove"),
                b -> send(BotActionPacket.REMOVE)));
        this.addButton(new Button(cx + 2, top + 82, 98, 20,
                new TranslationTextComponent("gui.virtualplayer.remove_all"),
                b -> send(BotActionPacket.REMOVE_ALL)));

        // ask the server for the current list
        Network.CHANNEL.sendToServer(new BotActionPacket(BotActionPacket.LIST, ""));
    }

    private void send(byte action) {
        String name = this.nameField == null ? "" : this.nameField.getText();
        Network.CHANNEL.sendToServer(new BotActionPacket(action, name));
    }

    @Override
    public void tick() {
        if (this.nameField != null) this.nameField.tick();
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.minecraft != null) this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        int cx = this.width / 2;
        int top = this.height / 2 - 70;

        drawCenteredString(ms, this.font, this.title, cx, top, 0xFFFFFF);
        drawString(ms, this.font, new TranslationTextComponent("gui.virtualplayer.name"), cx - 100, top + 18, 0xA0A0A0);
        this.nameField.render(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);

        List<String> bots = BotClientState.getBots();
        drawCenteredString(ms, this.font, new TranslationTextComponent("gui.virtualplayer.active"), cx, top + 112, 0xFFFFFF);
        String list = bots.isEmpty()
                ? new TranslationTextComponent("gui.virtualplayer.none").getString()
                : String.join(", ", bots);
        drawCenteredString(ms, this.font, this.font.trimStringToWidth(list, 300), cx, top + 126, 0x55FF55);
    }
}
