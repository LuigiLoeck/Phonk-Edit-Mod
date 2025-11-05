package com.luigi.phonkeditmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(
			FabricLoader.getInstance().getConfigDir().toFile(),
			"phonk-edit-mod.json"
	);

	// Timer Settings
	public int minSegundosEntreEfeitos = 30;
	public int maxSegundosEntreEfeitos = 60;
	public int duracaoEfeitoSegundos = 3;
	public int delayAcaoMs = 150;
	
	// Trigger Settings
	public float chanceAtivarPorAcao = 0.30f; // 30%
	public boolean habilitarTimerAleatorio = true;
	public boolean timerIgnoraChance = false; // Timer ignora a chance de trigger
	public boolean habilitarTriggerAtaque = true;
	public boolean habilitarTriggerQuebrarBloco = true;
	public boolean habilitarTriggerUsarBloco = true; // Interagir/colocar blocos (right-click em bloco)
	public boolean habilitarTriggerUsarItem = true; // Comer, beber, arco, etc
	public boolean habilitarTriggerTomarDano = true;
	
	// Audio Settings
	public float pitchMinimo = 0.2f;
	public float pitchMaximo = 2.0f;
	public float volumeMusica = 1.0f;
	public ResourceMode audioMode = ResourceMode.MOD_ONLY; // Modo de áudio
	public ResourceMode imageMode = ResourceMode.MOD_ONLY; // Modo de imagem
	
	// Visual Effects Settings
	public int tamanhoIcone = 48; // pixels
	public boolean habilitarGrayscale = true;
	public boolean habilitarBlur = true;
	public boolean habilitarZoom = true;
	public boolean habilitarShake = true;
	public boolean habilitarBarrasPretas = true;
	public boolean habilitarTextoMeme = true;
	public boolean habilitarIconeCaveira = true;
	
	// Effect Intensity
	public float intensidadeZoom = 1.0f; // Multiplier (0.5 - 2.0)
	public float intensidadeBlur = 1.0f; // Multiplier (0.5 - 2.0)
	public float intensidadeShake = 1.0f; // Multiplier (0.5 - 2.0)

	public static ModConfig load() {
		if (!CONFIG_FILE.exists()) {
			ModConfig config = new ModConfig();
			config.save();
			return config;
		}

		try (FileReader reader = new FileReader(CONFIG_FILE)) {
			return GSON.fromJson(reader, ModConfig.class);
		} catch (IOException e) {
			System.err.println("Erro ao carregar config do Phonk Edit Mod: " + e.getMessage());
			return new ModConfig();
		}
	}

	public void save() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			System.err.println("Erro ao salvar config do Phonk Edit Mod: " + e.getMessage());
		}
	}
	
	/**
	 * Enum para definir o modo de uso de recursos (áudio/imagem)
	 */
	public enum ResourceMode {
		MOD_ONLY("Mod Only"),           // Usa somente os recursos do mod
		MIX("Mix"),                      // Mix: alterna aleatoriamente entre mod e custom
		CUSTOM_ONLY("Custom Only");     // Usa somente recursos customizados
		
		private final String displayName;
		
		ResourceMode(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public ResourceMode next() {
			ResourceMode[] values = values();
			return values[(this.ordinal() + 1) % values.length];
		}
	}
}
