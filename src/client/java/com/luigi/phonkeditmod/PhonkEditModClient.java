package com.luigi.phonkeditmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import java.util.Random;

// SATIN API
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

public class PhonkEditModClient implements ClientModInitializer {

	// --- Configuração ---
	private static final int MIN_TICKS_ENTRE_MEMES = 20 * 5; // 5 segundos
	private static final int MAX_TICKS_ENTRE_MEMES = 20 * 20; // 20 segundos
	private static final int DURACAO_MEME_TICKS = 20 * 3;     // 3 segundos
	private static final int MEME_RENDER_SIZE = 64;
	private static final int MEME_TEXTURE_SIZE = 512;

	// Lista dos seus assets
	private static final Identifier[] MEME_IMAGES = new Identifier[]{
			Identifier.of("phonk-edit-mod", "textures/gui/caveira1.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira2.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira3.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira4.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira5.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira6.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira7.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira8.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira9.png"),
			Identifier.of("phonk-edit-mod", "textures/gui/caveira10.png")
			// Adicione mais...
	};

	// Assuma que você registrou "phonk1", "phonk2" no seu sounds.json
	private static final SoundEvent[] MEME_SOUNDS = new SoundEvent[]{
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk1")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk2")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk3")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk4")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk5")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk6")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk7")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk8")),
			SoundEvent.of(Identifier.of("phonk-edit-mod", "phonk9"))
			// Adicione mais...
	};

	// --- Variáveis de Estado ---
	private final Random random = new Random();
	private boolean isMemeActive = false;
	private int ticksParaProximoMeme = -1;
	private int ticksMemeAtivo = 0;
	private Identifier imagemMemeAtual;
	private net.minecraft.client.sound.SoundInstance somMemeAtual = null;
	
	// SHADER SATIN
	private static final ManagedShaderEffect GRAYSCALE_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("phonk-edit-mod", "shaders/post/grayscale.json"));

	@Override
	public void onInitializeClient() {
		// 1. Registra o "Ouvinte" do Timer (Tick)
		registerTickTimer();

		// 2. Registra o "Ouvinte" da Renderização (HUD)
		registerHudRenderer();
	}

	private void registerTickTimer() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Só roda se o jogador estiver no mundo
			if (client.player == null) {
				// Reseta o timer se o jogador sair do mundo
				ticksParaProximoMeme = -1;
				pararMeme(client); // Garante que o meme pare
				return;
			}

			// Se o meme está rolando
			if (isMemeActive) {
				ticksMemeAtivo--;
				if (ticksMemeAtivo <= 0) {
					pararMeme(client);
				}
			}
			// Se o meme não está rolando
			else {
				// Inicia o timer pela primeira vez
				if (ticksParaProximoMeme == -1) {
					ticksParaProximoMeme = getTempoAleatorio();
				}

				ticksParaProximoMeme--;

				// Hora de ativar o meme!
				if (ticksParaProximoMeme <= 0) {
					iniciarMeme(client);
				}
			}
		});
	}

// Dentro de MeuModClient.java

	private void iniciarMeme(MinecraftClient client) {
		isMemeActive = true;
		ticksMemeAtivo = DURACAO_MEME_TICKS;
		ticksParaProximoMeme = getTempoAleatorio();

		// 1. PAUSA O JOGO
		client.setScreen(new InvisiblePauseScreen());

		// 2. Toca um Phonk Aleatório
		SoundEvent som = MEME_SOUNDS[random.nextInt(MEME_SOUNDS.length)];
		this.somMemeAtual = PositionedSoundInstance.master(som, 1.0f);
		client.getSoundManager().play(this.somMemeAtual);

		// 3. Seleciona uma Imagem Aleatória
		imagemMemeAtual = MEME_IMAGES[random.nextInt(MEME_IMAGES.length)];
	}

	private void pararMeme(MinecraftClient client) {
		if (!isMemeActive) return;

		isMemeActive = false;
		ticksMemeAtivo = 0;

		// 1. DESPAUSA O JOGO
		client.setScreen(null);

		// 2. Para o som
		if (this.somMemeAtual != null) {
			client.getSoundManager().stop(this.somMemeAtual);
			this.somMemeAtual = null;
		}
	}

	private void registerHudRenderer() {
		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
			if (!isMemeActive) return;

			MinecraftClient client = MinecraftClient.getInstance();
			int screenWidth = client.getWindow().getScaledWidth();
			int screenHeight = client.getWindow().getScaledHeight();

			// Desenha a caveira
			if (imagemMemeAtual != null) {
				int x = (screenWidth - MEME_RENDER_SIZE) / 2;
				int y_center_point = (screenHeight * 3) / 4;
				int y = y_center_point - (MEME_RENDER_SIZE / 2);

				drawContext.drawTexture(
						imagemMemeAtual,
						x, y,
						MEME_RENDER_SIZE, MEME_RENDER_SIZE,
						0, 0,
						MEME_TEXTURE_SIZE, MEME_TEXTURE_SIZE,
						MEME_TEXTURE_SIZE, MEME_TEXTURE_SIZE
				);
			}
			
			// Aplica o shader de grayscale DEPOIS de tudo renderizado (incluindo HUD)
			GRAYSCALE_SHADER.render(tickCounter.getTickDelta(false));
		});
	}

	private int getTempoAleatorio() {
		return random.nextInt(MIN_TICKS_ENTRE_MEMES, MAX_TICKS_ENTRE_MEMES + 1);
	}
}