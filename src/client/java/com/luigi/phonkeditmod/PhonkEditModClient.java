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
	
	// Textos chamativos para o topo (estilo meme)
	private static final String[] MEME_TEXTS = new String[]{
			"THOSE WHO KNOW",
			"WHEN I ENTER THE GAME",
			"POV: YOU'RE TOO GOOD",
			"MOMENTS BEFORE DISASTER",
			"MINECRAFT EDIT 🔥",
			"THIS IS GOING TO BE EPIC",
			"SIGMA GRINDSET",
			"COLD AS ICE ❄️",
			"BRO...",
			"I AM INEVITABLE",
			"LITERALLY ME",
			"MAIN CHARACTER MOMENT",
			"NOBODY CAN STOP ME",
			"THIS IS HOW I PLAY",
			"LEGENDARY"
	};

	// --- Variáveis de Estado ---
	private static PhonkEditModClient instance; // Instância singleton
	private final Random random = new Random();
	private boolean isMemeActive = false;
	private int ticksParaProximoMeme = -1;
	private int ticksMemeAtivo = 0;
	private Identifier imagemMemeAtual;
	private net.minecraft.client.sound.SoundInstance somMemeAtual = null;
	private float vidaAnterior = -1; // Para detectar dano
	private String textoMemeAtual; // Texto chamativo atual
	
	// Efeitos de câmera
	private static boolean applyCameraEffects = false;
	private static float currentZoom = 1.0f;
	private static float targetZoom = 1.0f;
	private static float shakeIntensity = 0.0f;
	private static float blurIntensity = 0.0f; // Intensidade do blur radial (0.0 a 1.0)
	private static float effectProgress = 0.0f; // Progresso do efeito (0.0 a 1.0)
	
	// Sistema de batidas (beats)
	private static float currentPitch = 1.0f; // Pitch atual da música
	private static float beatProgress = 0.0f; // Progresso dentro da batida atual (0.0 a 1.0)
	private static int ticksPerBeat = 10; // Ticks por batida (baseado no pitch)
	
	// SHADER SATIN
	private static final ManagedShaderEffect GRAYSCALE_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("phonk-edit-mod", "shaders/post/grayscale.json"));
	private static final ManagedShaderEffect RADIAL_BLUR_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("phonk-edit-mod", "shaders/post/radial_blur.json"));
	private static final ManagedShaderEffect PASSTHROUGH_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("phonk-edit-mod", "shaders/post/blit.json"));

	@Override
	public void onInitializeClient() {
		// Salva instância singleton
		instance = this;
		
		// 0. Carrega as configurações
		config = ModConfig.load();
		
		// 1. Registra keybinding para abrir menu (tecla O)
		configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.phonk-edit-mod.config",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_O,
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
				
				// Calcula progresso do efeito (0.0 a 1.0)
				int duracaoTotal = config.duracaoEfeitoSegundos * 20;
				effectProgress = 1.0f - ((float) ticksMemeAtivo / (float) duracaoTotal);
				
				// Sistema de batidas baseado no pitch da música
				// Pitch mais alto = música mais rápida = mais batidas por segundo
				// Pitch mais baixo = música mais lenta = menos batidas por segundo
				
				// BPM base: ~140 BPM (phonk típico) = ~2.33 batidas/segundo = ~8.5 ticks/batida
				// Ajustamos pelo pitch: pitch 2.0 = 2x mais rápido = 2x mais batidas
				float baseBPM = 140.0f;
				float adjustedBPM = baseBPM * currentPitch;
				float beatsPerSecond = adjustedBPM / 60.0f;
				ticksPerBeat = (int) Math.max(3, 20.0f / beatsPerSecond); // Mínimo 3 ticks
				
				// Progresso dentro da batida atual (0.0 a 1.0, reseta a cada batida)
				int ticksInCurrentBeat = (duracaoTotal - ticksMemeAtivo) % ticksPerBeat;
				beatProgress = (float) ticksInCurrentBeat / (float) ticksPerBeat;
				
				// Curva de batida: pulso rápido no início, decai suavemente
				// Usa uma curva exponencial inversa para efeito de "impacto"
				float beatIntensity = (float) Math.pow(1.0f - beatProgress, 3.0); // Cúbica para decay rápido
				
				// Zoom pulsa com as batidas SE HABILITADO: 1.0 (normal) -> 1.3 (pico) em cada batida
				if (config.habilitarZoom) {
					targetZoom = 1.0f + (beatIntensity * 0.3f * config.intensidadeZoom);
				} else {
					targetZoom = 1.0f; // Sem zoom
				}
				
				// Blur pulsa junto com zoom SE HABILITADO: 0.0 -> 0.8 em cada batida
				if (config.habilitarBlur) {
					blurIntensity = beatIntensity * 0.8f * config.intensidadeBlur;
				} else {
					blurIntensity = 0.0f; // Sem blur
				}
				
				// Shake constante com picos nas batidas SE HABILITADO
				if (config.habilitarShake) {
					// Shake base + extra na batida
					float baseShake = 0.15f;
					float beatShake = beatIntensity * 0.25f;
					
					// Intensifica no final (últimos 10%)
					if (effectProgress > 0.9f) {
						float finalIntensity = (effectProgress - 0.9f) * 10.0f; // 0.0 a 1.0
						baseShake += finalIntensity * 0.3f; // Até +0.3
						beatShake += finalIntensity * 0.2f; // Mais violento
					}
					
					shakeIntensity = (baseShake + beatShake) * config.intensidadeShake;
				} else {
					shakeIntensity = 0.0f; // Sem shake
				}
				
				if (ticksMemeAtivo <= 0) {
					pararMeme(client);
				}
				
				// Zoom suave (interpolação)
				if (Math.abs(currentZoom - targetZoom) > 0.001f) {
					// Interpolação rápida para seguir as batidas
					currentZoom += (targetZoom - currentZoom) * 0.3f;
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
						// Se timer ignora chance, ativa direto
						// Senão, respeita a chance configurada
						if (config.timerIgnoraChance || random.nextFloat() < config.chanceAtivarPorAcao) {
							iniciarMeme(client);
						} else {
							// Não passou na chance, agenda novo timer
							ticksParaProximoMeme = getTempoAleatorio();
						}
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
		
		// Salva o pitch para calcular batidas
		currentPitch = pitchAleatorio;

		// 3. Seleciona uma Imagem Aleatória
		imagemMemeAtual = MEME_IMAGES[random.nextInt(MEME_IMAGES.length)];
		
		// 4. Seleciona um Texto Aleatório
		textoMemeAtual = MEME_TEXTS[random.nextInt(MEME_TEXTS.length)];
		
		// 5. Ativa efeitos de câmera (batidas de zoom + shake + blur)
		applyCameraEffects = true;
		currentZoom = 1.0f;
		targetZoom = 1.0f;
		shakeIntensity = 0.15f;
		blurIntensity = 0.0f;
		beatProgress = 0.0f;
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
		
		// 3. Desativa efeitos de câmera, blur e batidas
		applyCameraEffects = false;
		currentZoom = 1.0f;
		targetZoom = 1.0f;
		shakeIntensity = 0.0f;
		blurIntensity = 0.0f;
		effectProgress = 0.0f;
		beatProgress = 0.0f;
		currentPitch = 1.0f;
	}

	private void registerHudRenderer() {
		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
			if (!isMemeActive) return;
			
			MinecraftClient client = MinecraftClient.getInstance();
			int screenWidth = client.getWindow().getScaledWidth();
			int screenHeight = client.getWindow().getScaledHeight();
			
		// Aplica efeito de shake em TODA a tela (incluindo HUD) SE HABILITADO
		if (config.habilitarShake && applyCameraEffects && shakeIntensity > 0) {
			float shakeX = (float) (Math.random() - 0.5) * shakeIntensity * 10.0f;
			float shakeY = (float) (Math.random() - 0.5) * shakeIntensity * 10.0f;
			
			drawContext.getMatrices().push();
			drawContext.getMatrices().translate(shakeX, shakeY, 0);
		}
		
		// SEMPRE aplica shader grayscale para corrigir ordem de renderização
		// Intensidade 0.0 = invisível (colorido), 1.0 = preto e branco
		if (config.habilitarBarrasPretas || config.habilitarGrayscale) {
			float grayscaleIntensity = config.habilitarGrayscale ? 1.0f : 0.0f;
			GRAYSCALE_SHADER.setUniformValue("Intensity", grayscaleIntensity);
			GRAYSCALE_SHADER.render(tickCounter.getTickDelta(false));
		}
		
		// Aplica blur radial se intensidade > 0 E SE HABILITADO
		if (config.habilitarBlur && blurIntensity > 0.01f) {
			// Atualiza uniform do shader com intensidade atual
			RADIAL_BLUR_SHADER.setUniformValue("BlurIntensity", blurIntensity);
			RADIAL_BLUR_SHADER.render(tickCounter.getTickDelta(false));
		}
		
		// Desenha o texto chamativo no topo SE HABILITADO
		if (config.habilitarTextoMeme && textoMemeAtual != null) {
			int textY = 30;
			int textColor = 0xFFFFFF; // Branco
			int shadowColor = 0x000000; // Preto
			
			// Desenha o texto centralizado com sombra
			int textWidth = client.textRenderer.getWidth(textoMemeAtual);
			int textX = (screenWidth - textWidth) / 2;
			
			// Sombra (offset)
			drawContext.drawText(client.textRenderer, textoMemeAtual, textX + 2, textY + 2, shadowColor, false);
			// Texto principal
			drawContext.drawText(client.textRenderer, textoMemeAtual, textX, textY, textColor, false);
		}
		
		// DEPOIS desenha a caveira colorida POR CIMA do shader SE HABILITADO
		if (config.habilitarIconeCaveira && imagemMemeAtual != null) {
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
		
		// Restaura a matriz (pop) se aplicamos shake
		if (config.habilitarShake && applyCameraEffects && shakeIntensity > 0) {
			drawContext.getMatrices().pop();
		}
		
		// Desenha barras pretas POR ÚLTIMO (depois de tudo, incluindo shaders) SE HABILITADO
		if (config.habilitarBarrasPretas) {
			// Calcula a largura das barras pretas (formato 9:16 - mobile)
			// Mantém proporção vertical 9:16
			int targetWidth = (screenHeight * 9) / 16;
			int barWidth = (screenWidth - targetWidth) / 2;
			
			// Desenha barras pretas nas laterais (se necessário)
			if (barWidth > 0) {
				// Barra esquerda
				drawContext.fill(0, 0, barWidth, screenHeight, 0xFF000000);
				// Barra direita
				drawContext.fill(screenWidth - barWidth, 0, screenWidth, screenHeight, 0xFF000000);
			}
		}
	});
}	private int getTempoAleatorio() {
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
	
	// Métodos estáticos para os mixins acessarem
	public static boolean shouldApplyCameraEffects() {
		return applyCameraEffects;
	}
	
	public static float getZoomLevel() {
		return currentZoom;
	}
	
	public static float getShakeIntensity() {
		return shakeIntensity;
	}
	
	public static float getBlurIntensity() {
		return blurIntensity;
	}
	
	public static float getEffectProgress() {
		return effectProgress;
	}
	
	// Método público para reiniciar o timer (chamado quando configs de tempo mudam)
	public static void reiniciarTimer() {
		if (instance != null && !instance.isMemeActive) {
			instance.ticksParaProximoMeme = instance.getTempoAleatorio();
		}
	}
	
	public static float getBeatProgress() {
		return beatProgress;
	}
}