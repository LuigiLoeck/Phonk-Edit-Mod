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

	// Configurações
	public int minSegundosEntreEfeitos = 30;
	public int maxSegundosEntreEfeitos = 60;
	public int duracaoEfeitoSegundos = 3;
	public float chanceAtivarPorAcao = 0.30f; // 30%
	public int delayAcaoMs = 150;
	public int tamanhoIcone = 48; // pixels
	public float pitchMinimo = 0.2f;
	public float pitchMaximo = 2.0f;
	
	// Ativar/desativar triggers
	public boolean habilitarTimerAleatorio = true;
	public boolean habilitarTriggerAtaque = true;
	public boolean habilitarTriggerQuebrarBloco = true;
	public boolean habilitarTriggerUsarBloco = true;
	public boolean habilitarTriggerTomarDano = true;

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
}
