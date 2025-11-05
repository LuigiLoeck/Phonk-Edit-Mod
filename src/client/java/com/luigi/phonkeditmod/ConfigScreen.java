package com.luigi.phonkeditmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final ModConfig config;
	private double scrollOffset = 0;
	private static final int SCROLL_SPEED = 10;
	private int contentHeight = 0; // Altura real do conteúdo (será calculada dinamicamente)

	public ConfigScreen(Screen parent, ModConfig config) {
		super(Text.literal("Phonk Edit Mod - Settings"));
		this.parent = parent;
		this.config = config;
	}

	@Override
	protected void init() {
		this.clearChildren();
		
		int centerX = this.width / 2;
		int startY = 40;
		int spacing = 25;
		int currentY = startY - (int) scrollOffset;

		// Espaço para o título
		currentY += spacing;

		// === TIMER SETTINGS ===
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Min Time: " + config.minSegundosEntreEfeitos + "s"),
				(double) config.minSegundosEntreEfeitos / 120.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Min Time: " + config.minSegundosEntreEfeitos + "s"));
			}

			@Override
			protected void applyValue() {
				int newValue = (int) (this.value * 120);
				if (newValue < 5) newValue = 5;
				// Limita para não ultrapassar o max
				if (newValue > config.maxSegundosEntreEfeitos) {
					newValue = config.maxSegundosEntreEfeitos;
					// Força o slider a voltar para o valor máximo permitido
					this.value = (double) newValue / 120.0;
				}
				config.minSegundosEntreEfeitos = newValue;
				PhonkEditModClient.reiniciarTimer();
				updateMessage();
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Max Time: " + config.maxSegundosEntreEfeitos + "s"),
				(double) config.maxSegundosEntreEfeitos / 180.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Max Time: " + config.maxSegundosEntreEfeitos + "s"));
			}

			@Override
			protected void applyValue() {
				int newValue = (int) (this.value * 180);
				if (newValue < 10) newValue = 10;
				// Limita para não ficar menor que o min
				if (newValue < config.minSegundosEntreEfeitos) {
					newValue = config.minSegundosEntreEfeitos;
					// Força o slider a voltar para o valor mínimo permitido
					this.value = (double) newValue / 180.0;
				}
				config.maxSegundosEntreEfeitos = newValue;
				PhonkEditModClient.reiniciarTimer();
				updateMessage();
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Effect Duration: " + config.duracaoEfeitoSegundos + "s"),
				(double) (config.duracaoEfeitoSegundos - 1) / 4.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Effect Duration: " + config.duracaoEfeitoSegundos + "s"));
			}

			@Override
			protected void applyValue() {
				config.duracaoEfeitoSegundos = 1 + (int) (this.value * 4);
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Action Delay: " + config.delayAcaoMs + "ms"),
				(double) config.delayAcaoMs / 1000.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Action Delay: " + config.delayAcaoMs + "ms"));
			}

			@Override
			protected void applyValue() {
				config.delayAcaoMs = (int) (this.value * 1000);
			}
		});
		currentY += spacing + 10;

		// === TRIGGER SETTINGS ===
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Trigger Chance: " + (int) (config.chanceAtivarPorAcao * 100) + "%"),
				config.chanceAtivarPorAcao
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Trigger Chance: " + (int) (config.chanceAtivarPorAcao * 100) + "%"));
			}

			@Override
			protected void applyValue() {
				config.chanceAtivarPorAcao = (float) this.value;
			}
		});
		currentY += spacing;

		// Linha 1: Random Timer + Timer Ignore Chance
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Random Timer: " + (config.habilitarTimerAleatorio ? "ON" : "OFF")),
				button -> {
					config.habilitarTimerAleatorio = !config.habilitarTimerAleatorio;
					button.setMessage(Text.literal("Random Timer: " + (config.habilitarTimerAleatorio ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Timer Ignore Chance: " + (config.timerIgnoraChance ? "ON" : "OFF")),
				button -> {
					config.timerIgnoraChance = !config.timerIgnoraChance;
					button.setMessage(Text.literal("Timer Ignore Chance: " + (config.timerIgnoraChance ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Linha 2: Attack Trigger + Block Break
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Attack Trigger: " + (config.habilitarTriggerAtaque ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerAtaque = !config.habilitarTriggerAtaque;
					button.setMessage(Text.literal("Attack Trigger: " + (config.habilitarTriggerAtaque ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Block Break: " + (config.habilitarTriggerQuebrarBloco ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerQuebrarBloco = !config.habilitarTriggerQuebrarBloco;
					button.setMessage(Text.literal("Block Break: " + (config.habilitarTriggerQuebrarBloco ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Linha 3: Block Place/Interact + Use Item
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Block Place/Interact: " + (config.habilitarTriggerUsarBloco ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerUsarBloco = !config.habilitarTriggerUsarBloco;
					button.setMessage(Text.literal("Block Place/Interact: " + (config.habilitarTriggerUsarBloco ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Use Item: " + (config.habilitarTriggerUsarItem ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerUsarItem = !config.habilitarTriggerUsarItem;
					button.setMessage(Text.literal("Use Item: " + (config.habilitarTriggerUsarItem ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;
		
		// Linha 4: Damage Trigger (centralizado)
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Damage Trigger: " + (config.habilitarTriggerTomarDano ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerTomarDano = !config.habilitarTriggerTomarDano;
					button.setMessage(Text.literal("Damage Trigger: " + (config.habilitarTriggerTomarDano ? "ON" : "OFF")));
				}
		).dimensions(centerX - 72, currentY, 145, 20).build());
		currentY += spacing + 10;

		// === AUDIO SETTINGS ===
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Min Pitch: " + String.format("%.1fx", config.pitchMinimo)),
				(config.pitchMinimo - 0.1) / 2.9
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Min Pitch: " + String.format("%.1fx", config.pitchMinimo)));
			}

			@Override
			protected void applyValue() {
				float newValue = (float) (0.1 + this.value * 2.9);
				// Limita para não ultrapassar o max
				if (newValue > config.pitchMaximo) {
					newValue = config.pitchMaximo;
					// Força o slider a voltar para o valor máximo permitido
					this.value = (newValue - 0.1) / 2.9;
				}
				config.pitchMinimo = newValue;
				updateMessage();
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Max Pitch: " + String.format("%.1fx", config.pitchMaximo)),
				(config.pitchMaximo - 0.1) / 2.9
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Max Pitch: " + String.format("%.1fx", config.pitchMaximo)));
			}

			@Override
			protected void applyValue() {
				float newValue = (float) (0.1 + this.value * 2.9);
				// Limita para não ficar menor que o min
				if (newValue < config.pitchMinimo) {
					newValue = config.pitchMinimo;
					// Força o slider a voltar para o valor mínimo permitido
					this.value = (newValue - 0.1) / 2.9;
				}
				config.pitchMaximo = newValue;
				updateMessage();
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Music Volume: " + (int) (config.volumeMusica * 100) + "%"),
				config.volumeMusica
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Music Volume: " + (int) (config.volumeMusica * 100) + "%"));
			}

			@Override
			protected void applyValue() {
				config.volumeMusica = (float) this.value;
			}
		});
		currentY += spacing;
		
		// === CUSTOM RESOURCES SETTINGS ===
		// Linha 1: Open Audio Folder + Audio Mode
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Open Audio Folder"),
				btn -> {
					// Abre o resource pack de audios customizados
					Path minecraftDir = Paths.get(System.getProperty("user.dir"));
					Path resourcepacksDir = minecraftDir.resolve("resourcepacks");
					Path tutorialPackDir = resourcepacksDir.resolve("PhonkEdit-CustomSongs");
					Path customSoundsDir = tutorialPackDir.resolve("assets/phonk-edit-mod/sounds/custom");
					
					// Se não existir, cria
					try {
						Files.createDirectories(customSoundsDir);
					} catch (IOException e) {
						System.err.println("[Phonk Edit Mod] Erro ao criar pasta: " + e.getMessage());
					}
					
					openFolder(customSoundsDir.toFile());
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Audio Mode: " + config.audioMode.getDisplayName()),
				button -> {
					config.audioMode = config.audioMode.next();
					button.setMessage(Text.literal("Audio Mode: " + config.audioMode.getDisplayName()));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;
		
		// Linha 2: Open Images Folder + Image Mode
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Open Images Folder"),
				btn -> openFolder(CustomResourceManager.getCustomImagesDirectory().toFile())
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Image Mode: " + config.imageMode.getDisplayName()),
				button -> {
					config.imageMode = config.imageMode.next();
					button.setMessage(Text.literal("Image Mode: " + config.imageMode.getDisplayName()));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;
		
		// Linha 3: Reload Custom Files (centralizado)
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Reload Custom Files"),
				button -> {
					// Recarrega os resource packs do Minecraft (equivalente ao F3+T)
					// Isso automaticamente vai disparar o listener que detecta sons e mostra popup
					MinecraftClient client = MinecraftClient.getInstance();
					if (client != null) {
						button.setMessage(Text.literal("⏳ Reloading..."));
						client.reloadResources();
						
						// Reseta a mensagem após 1 segundo usando scheduler
						PhonkEditModClient.scheduleTask(() -> 
							client.execute(() -> button.setMessage(Text.literal("Reload Custom Files")))
						, 1000);
					}
				}
		).dimensions(centerX - 150, currentY, 300, 20).build());
		currentY += spacing + 10;

		// === VISUAL EFFECTS ===
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Icon Size: " + config.tamanhoIcone + "px"),
				(config.tamanhoIcone - 16.0) / 112.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Icon Size: " + config.tamanhoIcone + "px"));
			}

			@Override
			protected void applyValue() {
				config.tamanhoIcone = 16 + (int) (this.value * 112);
			}
		});
		currentY += spacing;

		// Linha 1: Grayscale + Radial Blur
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Grayscale: " + (config.habilitarGrayscale ? "ON" : "OFF")),
				button -> {
					config.habilitarGrayscale = !config.habilitarGrayscale;
					button.setMessage(Text.literal("Grayscale: " + (config.habilitarGrayscale ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Radial Blur: " + (config.habilitarBlur ? "ON" : "OFF")),
				button -> {
					config.habilitarBlur = !config.habilitarBlur;
					button.setMessage(Text.literal("Radial Blur: " + (config.habilitarBlur ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Linha 2: Camera Zoom + Camera Shake
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Camera Zoom: " + (config.habilitarZoom ? "ON" : "OFF")),
				button -> {
					config.habilitarZoom = !config.habilitarZoom;
					button.setMessage(Text.literal("Camera Zoom: " + (config.habilitarZoom ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Camera Shake: " + (config.habilitarShake ? "ON" : "OFF")),
				button -> {
					config.habilitarShake = !config.habilitarShake;
					button.setMessage(Text.literal("Camera Shake: " + (config.habilitarShake ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Linha 3: Black Bars + Meme Text
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Black Bars: " + (config.habilitarBarrasPretas ? "ON" : "OFF")),
				button -> {
					config.habilitarBarrasPretas = !config.habilitarBarrasPretas;
					button.setMessage(Text.literal("Black Bars: " + (config.habilitarBarrasPretas ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Meme Text: " + (config.habilitarTextoMeme ? "ON" : "OFF")),
				button -> {
					config.habilitarTextoMeme = !config.habilitarTextoMeme;
					button.setMessage(Text.literal("Meme Text: " + (config.habilitarTextoMeme ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Linha 4: Skull Icon (centralizado)
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Skull Icon: " + (config.habilitarIconeCaveira ? "ON" : "OFF")),
				button -> {
					config.habilitarIconeCaveira = !config.habilitarIconeCaveira;
					button.setMessage(Text.literal("Skull Icon: " + (config.habilitarIconeCaveira ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 300, 20).build());
		currentY += spacing + 10;

		// === EFFECT INTENSITY ===
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Zoom Intensity: " + String.format("%.1fx", config.intensidadeZoom)),
				(config.intensidadeZoom - 0.5) / 1.5
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Zoom Intensity: " + String.format("%.1fx", config.intensidadeZoom)));
			}

			@Override
			protected void applyValue() {
				config.intensidadeZoom = (float) (0.5 + this.value * 1.5);
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Blur Intensity: " + String.format("%.1fx", config.intensidadeBlur)),
				(config.intensidadeBlur - 0.5) / 1.5
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Blur Intensity: " + String.format("%.1fx", config.intensidadeBlur)));
			}

			@Override
			protected void applyValue() {
				config.intensidadeBlur = (float) (0.5 + this.value * 1.5);
			}
		});
		currentY += spacing;

		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Shake Intensity: " + String.format("%.1fx", config.intensidadeShake)),
				(config.intensidadeShake - 0.5) / 1.5
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Shake Intensity: " + String.format("%.1fx", config.intensidadeShake)));
			}

			@Override
			protected void applyValue() {
				config.intensidadeShake = (float) (0.5 + this.value * 1.5);
			}
		});
		currentY += spacing * 3;
		
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Done"),
				button -> this.close()
		).dimensions(centerX - 100, currentY, 200, 20).build());
		
		currentY += spacing; // Espaço final após o botão
		
		// Salva a altura total do conteúdo (a posição Y final + offset inicial do scroll)
		contentHeight = currentY + (int) scrollOffset;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		
		// Renderiza os widgets scrolláveis
		super.render(context, mouseX, mouseY, delta);
		
		// Título - renderizado dentro do scroll com espaçamento adequado
		int titleY = 40 + 10 - (int) scrollOffset; // Posição do título com espaço
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, titleY, 0xFFFFFF);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scrollOffset -= verticalAmount * SCROLL_SPEED;
		
		// Calcula o scroll máximo: só pode scrollar até o último elemento ficar na parte de baixo da tela
		// contentHeight = altura total do conteúdo
		// this.height = altura da janela
		// Só pode scrollar se contentHeight > this.height
		int maxScroll = Math.max(0, contentHeight - this.height);
		
		// Limita o scroll
		scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
		
		this.init();
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_UP) {
			mouseScrolled(0, 0, 0, 1);
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_DOWN) {
			mouseScrolled(0, 0, 0, -1);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		config.save();
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}
	
	/**
	 * Abre uma pasta no explorador de arquivos do sistema
	 */
	private void openFolder(java.io.File folder) {
		try {
			// Usa o utilitário do Minecraft para abrir URLs/arquivos
			// Isso funciona cross-platform (Windows, Linux, Mac)
			Util.getOperatingSystem().open(folder);
		} catch (Exception e) {
			System.err.println("[Phonk Edit Mod] Erro ao abrir pasta: " + e.getMessage());
			
			// Fallback: tenta abrir usando Desktop API do Java
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(folder);
				}
			} catch (IOException ex) {
				System.err.println("[Phonk Edit Mod] Erro no fallback ao abrir pasta: " + ex.getMessage());
			}
		}
	}
}
