package com.luigi.phonkeditmod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

// Esta é uma tela que não desenha NADA
public class InvisiblePauseScreen extends Screen {

    public InvisiblePauseScreen() {
        // O 'super(..)' é necessário, mas o texto não importa
        super(Text.literal("MEME_PAUSE"));
        // IMPORTANTE: passamos 'true' no construtor da Screen
        // para dizer ao jogo que esta tela DEVE pausar o jogo.
    }

    // Sobrescrevemos o método de renderização para não desenhar NADA.
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Vazio de propósito
    }

    // Impede que o 'Esc' feche a tela (nós controlamos quando ela fecha)
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    // Diz ao jogo que o jogo deve pausar
    @Override
    public boolean shouldPause() {
        return true;
    }
}
