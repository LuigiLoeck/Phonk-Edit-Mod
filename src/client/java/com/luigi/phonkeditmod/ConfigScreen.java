package com.luigi.phonkeditmod;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final ModConfig config;

	public ConfigScreen(Screen parent, ModConfig config) {
		super(Text.literal("Phonk Edit Mod - Configurações"));
		this.parent = parent;
		this.config = config;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int startY = 40;
		int spacing = 25;
		int currentY = startY;

		// Slider: Min tempo entre efeitos
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Min Tempo: " + config.minSegundosEntreEfeitos + "s"),
				(double) config.minSegundosEntreEfeitos / 120.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Min Tempo: " + config.minSegundosEntreEfeitos + "s"));
			}

			@Override
			protected void applyValue() {
				config.minSegundosEntreEfeitos = (int) (this.value * 120); // 0-120 segundos
				if (config.minSegundosEntreEfeitos < 5) config.minSegundosEntreEfeitos = 5;
			}
		});
		currentY += spacing;

		// Slider: Max tempo entre efeitos
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Max Tempo: " + config.maxSegundosEntreEfeitos + "s"),
				(double) config.maxSegundosEntreEfeitos / 180.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Max Tempo: " + config.maxSegundosEntreEfeitos + "s"));
			}

			@Override
			protected void applyValue() {
				config.maxSegundosEntreEfeitos = (int) (this.value * 180); // 0-180 segundos
				if (config.maxSegundosEntreEfeitos < 10) config.maxSegundosEntreEfeitos = 10;
			}
		});
		currentY += spacing;

		// Slider: Duração do efeito
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Duração: " + config.duracaoEfeitoSegundos + "s"),
				(double) config.duracaoEfeitoSegundos / 10.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Duração: " + config.duracaoEfeitoSegundos + "s"));
			}

			@Override
			protected void applyValue() {
				config.duracaoEfeitoSegundos = (int) (this.value * 10); // 0-10 segundos
				if (config.duracaoEfeitoSegundos < 1) config.duracaoEfeitoSegundos = 1;
			}
		});
		currentY += spacing;

		// Slider: Chance por ação
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Chance Ação: " + (int)(config.chanceAtivarPorAcao * 100) + "%"),
				config.chanceAtivarPorAcao
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Chance Ação: " + (int)(config.chanceAtivarPorAcao * 100) + "%"));
			}

			@Override
			protected void applyValue() {
				config.chanceAtivarPorAcao = (float) this.value;
			}
		});
		currentY += spacing;

		// Slider: Tamanho do ícone
		this.addDrawableChild(new SliderWidget(
				centerX - 150, currentY, 300, 20,
				Text.literal("Tamanho Ícone: " + config.tamanhoIcone + "px"),
				(double) config.tamanhoIcone / 128.0
		) {
			@Override
			protected void updateMessage() {
				setMessage(Text.literal("Tamanho Ícone: " + config.tamanhoIcone + "px"));
			}

			@Override
			protected void applyValue() {
				config.tamanhoIcone = (int) (this.value * 128); // 0-128 pixels
				if (config.tamanhoIcone < 16) config.tamanhoIcone = 16;
			}
		});
		currentY += spacing;

		// Toggle: Timer aleatório
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Timer: " + (config.habilitarTimerAleatorio ? "ON" : "OFF")),
				button -> {
					config.habilitarTimerAleatorio = !config.habilitarTimerAleatorio;
					button.setMessage(Text.literal("Timer: " + (config.habilitarTimerAleatorio ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());

		// Toggle: Trigger Ataque
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Ataque: " + (config.habilitarTriggerAtaque ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerAtaque = !config.habilitarTriggerAtaque;
					button.setMessage(Text.literal("Ataque: " + (config.habilitarTriggerAtaque ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Toggle: Trigger Quebrar Bloco
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Quebrar: " + (config.habilitarTriggerQuebrarBloco ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerQuebrarBloco = !config.habilitarTriggerQuebrarBloco;
					button.setMessage(Text.literal("Quebrar: " + (config.habilitarTriggerQuebrarBloco ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());

		// Toggle: Trigger Usar Bloco
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Colocar: " + (config.habilitarTriggerUsarBloco ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerUsarBloco = !config.habilitarTriggerUsarBloco;
					button.setMessage(Text.literal("Colocar: " + (config.habilitarTriggerUsarBloco ? "ON" : "OFF")));
				}
		).dimensions(centerX + 5, currentY, 145, 20).build());
		currentY += spacing;

		// Toggle: Trigger Tomar Dano
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Dano: " + (config.habilitarTriggerTomarDano ? "ON" : "OFF")),
				button -> {
					config.habilitarTriggerTomarDano = !config.habilitarTriggerTomarDano;
					button.setMessage(Text.literal("Dano: " + (config.habilitarTriggerTomarDano ? "ON" : "OFF")));
				}
		).dimensions(centerX - 150, currentY, 145, 20).build());
		currentY += spacing + 10;

		// Botão: Salvar e Voltar
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Salvar e Voltar"),
				button -> {
					config.save();
					if (this.client != null) {
						this.client.setScreen(parent);
					}
				}
		).dimensions(centerX - 100, currentY, 200, 20).build());
	}

	@Override
	public void close() {
		config.save();
		if (this.client != null) {
			this.client.setScreen(parent);
		}
	}

	@Override
	public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		
		// Título
		context.drawCenteredTextWithShadow(
				this.textRenderer,
				this.title,
				this.width / 2,
				20,
				0xFFFFFF
		);
	}
}
