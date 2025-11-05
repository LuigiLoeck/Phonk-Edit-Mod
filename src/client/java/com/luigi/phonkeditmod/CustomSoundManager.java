package com.luigi.phonkeditmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia os sons customizados do mod.
 * Registra SoundEvents dinamicamente para arquivos OGG externos.
 */
public class CustomSoundManager {
	
	private static final List<SoundEvent> customSoundEvents = new ArrayList<>();
	private static final List<Path> customSoundPaths = new ArrayList<>();
	
	/**
	 * Carrega e registra os sons customizados
	 */
	public static void loadCustomSounds(List<CustomResourceManager.CustomSound> customSounds) {
		// Limpa sons anteriores
		customSoundEvents.clear();
		customSoundPaths.clear();
		
		if (customSounds.isEmpty()) {
			return;
		}
		
		// Registra cada som customizado
		for (int i = 0; i < customSounds.size(); i++) {
			CustomResourceManager.CustomSound customSound = customSounds.get(i);
			Path soundPath = customSound.getFilePath();
			
			try {
				// Verifica se o arquivo existe e é válido
				if (!Files.exists(soundPath) || !Files.isRegularFile(soundPath)) {
					System.err.println("[Phonk Edit Mod] Arquivo de som não encontrado: " + soundPath);
					continue;
				}
				
				// Cria um Identifier único para este som
				Identifier soundId = Identifier.of("phonk-edit-mod", "custom_sound_" + i);
				
				// Cria e registra o SoundEvent
				SoundEvent soundEvent = SoundEvent.of(soundId);
				
				// Adiciona às listas
				customSoundEvents.add(soundEvent);
				customSoundPaths.add(soundPath);
				
				System.out.println("[Phonk Edit Mod] Registrado som customizado: " + customSound.getFileName() + " -> " + soundId);
				
			} catch (Exception e) {
				System.err.println("[Phonk Edit Mod] Erro ao registrar som " + customSound.getFileName() + ": " + e.getMessage());
			}
		}
		
		System.out.println("[Phonk Edit Mod] Total de sons customizados carregados: " + customSoundEvents.size());
	}
	
	/**
	 * Retorna a lista de SoundEvents customizados
	 */
	public static List<SoundEvent> getCustomSoundEvents() {
		return customSoundEvents;
	}
	
	/**
	 * Retorna o caminho do arquivo de som para um índice específico
	 */
	public static Path getSoundPath(int index) {
		if (index >= 0 && index < customSoundPaths.size()) {
			return customSoundPaths.get(index);
		}
		return null;
	}
	
	/**
	 * Retorna o total de sons customizados carregados
	 */
	public static int getCustomSoundCount() {
		return customSoundEvents.size();
	}
	
	/**
	 * Verifica se há sons customizados carregados
	 */
	public static boolean hasCustomSounds() {
		return !customSoundEvents.isEmpty();
	}
}
