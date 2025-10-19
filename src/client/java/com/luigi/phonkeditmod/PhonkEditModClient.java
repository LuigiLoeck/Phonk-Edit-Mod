package com.luigi.phonkeditmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.ActionResult;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// SATIN API
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

public class PhonkEditModClient implements ClientModInitializer {

	// --- Configuração ---
	private static final int MEME_TEXTURE_SIZE = 512;
	private static ModConfig config; // Configurações do mod
	
	// Keybinding para abrir menu
	private static KeyBinding configKey;
	
	// Executor para delays
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
	private float vidaAnterior = -1; // Para detectar dano
	
	// SHADER SATIN
	private static final ManagedShaderEffect GRAYSCALE_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("phonk-edit-mod", "shaders/post/grayscale.json"));

	@Override
	public void onInitializeClient() {
		// 0. Carrega as configurações
		config = ModConfig.load();
		
		// 1. Registra keybinding para abrir menu (tecla P)
		configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.phonk-edit-mod.config",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_P,
				"category.phonk-edit-mod"
		));
		
		// 2. Registra o "Ouvinte" do Timer (Tick)
		registerTickTimer();

		// 3. Registra o "Ouvinte" da Renderização (HUD)
		registerHudRenderer();
		
		// 4. Registra os triggers de ação
		registerActionTriggers();
	}

	private void registerTickTimer() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Verifica se a tecla de config foi pressionada
			if (configKey.wasPressed()) {
				client.setScreen(new ConfigScreen(client.currentScreen, config));
			}
			
			// Só roda se o jogador estiver no mundo
			if (client.player == null) {
				// Reseta o timer se o jogador sair do mundo
				ticksParaProximoMeme = -1;
				pararMeme(client); // Garante que o meme pare
				vidaAnterior = -1;
				return;
			}

			// Detecta quando o jogador toma dano
			float vidaAtual = client.player.getHealth();
			if (vidaAnterior > 0 && vidaAtual < vidaAnterior) {
				// Jogador tomou dano!
				if (config.habilitarTriggerTomarDano) {
					tentarAtivarMemePorAcaoComDelay();
				}
			}
			vidaAnterior = vidaAtual;

			// NÃO CONTA TICKS SE O JOGO ESTIVER PAUSADO (menu aberto, mas não nosso InvisiblePauseScreen)
			if (client.isPaused() && !(client.currentScreen instanceof InvisiblePauseScreen)) {
				return; // Não conta tempo durante pause real
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
				// Só conta timer se estiver habilitado na config
				if (config.habilitarTimerAleatorio) {
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
			}
		});
	}

// Dentro de MeuModClient.java

	private void iniciarMeme(MinecraftClient client) {
		isMemeActive = true;
		ticksMemeAtivo = config.duracaoEfeitoSegundos * 20; // Converte segundos para ticks
		ticksParaProximoMeme = getTempoAleatorio();

		// 1. PAUSA O JOGO
		client.setScreen(new InvisiblePauseScreen());

		// 2. Toca um Phonk Aleatório com pitch variável (da config)
		SoundEvent som = MEME_SOUNDS[random.nextInt(MEME_SOUNDS.length)];
		float pitchRange = config.pitchMaximo - config.pitchMinimo;
		float pitchAleatorio = config.pitchMinimo + random.nextFloat() * pitchRange;
		this.somMemeAtual = PositionedSoundInstance.master(som, pitchAleatorio);
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
			
			// Aplica o shader de grayscale PRIMEIRO (em tudo que já foi renderizado)
			GRAYSCALE_SHADER.render(tickCounter.getTickDelta(false));
			
			// DEPOIS desenha a caveira colorida POR CIMA do shader
			if (imagemMemeAtual != null) {
				int screenWidth = client.getWindow().getScaledWidth();
				int screenHeight = client.getWindow().getScaledHeight();
				int renderSize = config.tamanhoIcone; // Usa o tamanho da config
				
				int x = (screenWidth - renderSize) / 2;
				int y_center_point = (screenHeight * 3) / 4;
				int y = y_center_point - (renderSize / 2);

				drawContext.drawTexture(
						imagemMemeAtual,
						x, y,
						renderSize, renderSize,
						0, 0,
						MEME_TEXTURE_SIZE, MEME_TEXTURE_SIZE,
						MEME_TEXTURE_SIZE, MEME_TEXTURE_SIZE
				);
			}
		});
	}

	private int getTempoAleatorio() {
		int minTicks = config.minSegundosEntreEfeitos * 20;
		int maxTicks = config.maxSegundosEntreEfeitos * 20;
		return random.nextInt(minTicks, maxTicks + 1);
	}
	
	private void registerActionTriggers() {
		// Trigger ao atacar/matar entidade (mob, jogador, etc) - COM DELAY
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (config.habilitarTriggerAtaque) {
				tentarAtivarMemePorAcaoComDelay();
			}
			return ActionResult.PASS;
		});
		
		// Trigger APÓS quebrar bloco (quando o bloco realmente quebra)
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (config.habilitarTriggerQuebrarBloco) {
				tentarAtivarMemePorAcaoComDelay();
			}
		});
		
		// Trigger ao colocar bloco / usar item - COM DELAY
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (config.habilitarTriggerUsarBloco) {
				tentarAtivarMemePorAcaoComDelay();
			}
			return ActionResult.PASS;
		});
	}
	
	/**
	 * Tenta ativar o meme após um delay, baseado em uma chance aleatória.
	 * Só ativa se o meme não estiver já ativo.
	 */
	private void tentarAtivarMemePorAcaoComDelay() {
		// Se já está ativo, não faz nada
		if (isMemeActive) return;
		
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return;
		
		// Chance aleatória de ativar (da config)
		if (random.nextFloat() < config.chanceAtivarPorAcao) {
			// Agenda a ativação após o delay configurado
			scheduler.schedule(() -> {
				// IMPORTANTE: Executa na thread de renderização para evitar crashes
				client.execute(() -> {
					// Verifica novamente se não está ativo (pode ter ativado no delay)
					if (!isMemeActive) {
						iniciarMeme(client);
					}
				});
			}, config.delayAcaoMs, TimeUnit.MILLISECONDS);
		}
	}
}