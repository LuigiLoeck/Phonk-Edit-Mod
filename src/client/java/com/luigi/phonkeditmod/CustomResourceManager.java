package com.luigi.phonkeditmod;

import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia recursos customizados (áudios e imagens) carregados de arquivos externos.
 * Suporta hot-reload sem reiniciar o jogo.
 */
public class CustomResourceManager {
	
	// Diretório para imagens customizadas
	private static final Path CUSTOM_IMAGES_DIR = FabricLoader.getInstance()
			.getGameDir().resolve("phonk-edit-mod").resolve("custom_images");
	
	// Lista de imagens customizadas
	private static final List<Identifier> customImages = new ArrayList<>();
	
	// Contadores de erros
	private static int lastImageErrors = 0;
	private static int lastAudioErrors = 0;
	
	/**
	 * Inicializa o diretório de imagens customizadas
	 */
	public static void initDirectories() {
		try {
			Files.createDirectories(CUSTOM_IMAGES_DIR);
			
			// Cria arquivo README na pasta
			createReadmeFile(CUSTOM_IMAGES_DIR,
				"Place your custom skull/meme images here (.png format).\n" +
				"Recommended size: 512x512 pixels (or any square resolution)\n" +
				"After adding files, click 'Reload Custom Files' in the mod config menu (Press O).");
			
		} catch (IOException e) {
			System.err.println("Erro ao criar diretório de imagens customizadas: " + e.getMessage());
		}
	}
	
	private static void createReadmeFile(Path directory, String content) {
		Path readmePath = directory.resolve("README.txt");
		if (!Files.exists(readmePath)) {
			try {
				Files.writeString(readmePath, content);
			} catch (IOException e) {
				System.err.println("Erro ao criar README: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Carrega todos os recursos customizados (somente imagens)
	 * Nota: Áudios customizados são carregados via resource packs
	 */
	public static void loadCustomResources() {
		System.out.println("[Phonk Edit Mod] Carregando imagens customizadas...");
		loadCustomImages();
		System.out.println("[Phonk Edit Mod] Imagens customizadas carregadas!");
	}
	
	/**
	 * Carrega imagens customizadas do diretório custom_images
	 */
	private static void loadCustomImages() {
		// Limpa texturas antigas do cache
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.getTextureManager() == null) {
			System.err.println("[Phonk Edit Mod] TextureManager não está disponível ainda. Pulando carregamento de imagens.");
			return;
		}
		
		for (Identifier id : customImages) {
			try {
				client.getTextureManager().destroyTexture(id);
			} catch (Exception e) {
				System.err.println("[Phonk Edit Mod] Erro ao destruir textura: " + e.getMessage());
			}
		}
		customImages.clear();
		lastImageErrors = 0; // Reseta contador de erros
		
		try {
			if (!Files.exists(CUSTOM_IMAGES_DIR)) {
				return;
			}
			
			// Lista TODOS os arquivos (não só PNG)
			List<Path> allFiles = Files.list(CUSTOM_IMAGES_DIR)
					.filter(Files::isRegularFile)
					.filter(path -> !path.getFileName().toString().equals("README.txt")) // Ignora README
					.collect(Collectors.toList());
			
			// Separa PNG dos outros
			List<Path> pngFiles = allFiles.stream()
					.filter(path -> path.toString().toLowerCase().endsWith(".png"))
					.collect(Collectors.toList());
			
			// Conta arquivos que NÃO são PNG
			int nonPngFiles = allFiles.size() - pngFiles.size();
			if (nonPngFiles > 0) {
				lastImageErrors = nonPngFiles;
				System.err.println("[Phonk Edit Mod] Encontrados " + nonPngFiles + " arquivo(s) em formato inválido (só PNG é suportado!)");
			}
			
			for (int i = 0; i < pngFiles.size(); i++) {
				Path imageFile = pngFiles.get(i);
				try {
					// Cria um Identifier único para esta imagem customizada
					Identifier imageId = Identifier.of("phonk-edit-mod", "custom_image_" + i);
					
					// Lê a imagem usando ImageIO (Java)
					BufferedImage bufferedImage = ImageIO.read(imageFile.toFile());
					
					if (bufferedImage == null) {
						System.err.println("[Phonk Edit Mod] Falha ao ler imagem: " + imageFile.getFileName());
						lastImageErrors++;
						continue;
					}
					
					// Converte para NativeImage (Minecraft)
					NativeImage nativeImage = convertToNativeImage(bufferedImage);
					
					// Registra a textura no TextureManager do Minecraft
					client.getTextureManager().registerTexture(
							imageId,
							new NativeImageBackedTexture(nativeImage)
					);
					
					customImages.add(imageId);
					System.out.println("[Phonk Edit Mod] Carregada imagem customizada: " + imageFile.getFileName());
					
				} catch (Exception e) {
					System.err.println("[Phonk Edit Mod] Erro ao carregar imagem " + imageFile.getFileName() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			System.err.println("[Phonk Edit Mod] Erro ao listar imagens customizadas: " + e.getMessage());
		}
	}
	
	/**
	 * Converte BufferedImage (Java AWT) para NativeImage (Minecraft)
	 */
	private static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		NativeImage nativeImage = new NativeImage(width, height, false);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = bufferedImage.getRGB(x, y);
				// BufferedImage usa ARGB, NativeImage usa ABGR
				int a = (rgb >> 24) & 0xFF;
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;
				
				// Converte para ABGR
				int abgr = (a << 24) | (b << 16) | (g << 8) | r;
				nativeImage.setColor(x, y, abgr);
			}
		}
		
		return nativeImage;
	}
	
	/**
	 * Retorna a lista de imagens customizadas
	 */
	public static List<Identifier> getCustomImages() {
		return customImages;
	}
	
	/**
	 * Retorna o número de erros de imagem do último carregamento
	 */
	public static int getLastImageErrors() {
		return lastImageErrors;
	}
	
	/**
	 * Retorna o número de erros de áudio do último carregamento
	 */
	public static int getLastAudioErrors() {
		return lastAudioErrors;
	}
	
	/**
	 * Detecta arquivos de áudio inválidos no resource pack
	 */
	public static int detectInvalidAudioFiles() {
		lastAudioErrors = 0;
		
		try {
			// Caminho para a pasta de sons customizados no resource pack
			Path minecraftDir = Paths.get(System.getProperty("user.dir"));
			Path resourcepacksDir = minecraftDir.resolve("resourcepacks");
			Path tutorialPackDir = resourcepacksDir.resolve("PhonkEdit-CustomSongs");
			Path customSoundsDir = tutorialPackDir.resolve("assets/phonk-edit-mod/sounds/custom");
			
			if (!Files.exists(customSoundsDir)) {
				return 0;
			}
			
			// Lista TODOS os arquivos
			List<Path> allFiles = Files.list(customSoundsDir)
					.filter(Files::isRegularFile)
					.filter(path -> !path.getFileName().toString().equals("PLACE_OGG_FILES_HERE.txt"))
					.filter(path -> !path.getFileName().toString().equals(".gitkeep"))
					.collect(Collectors.toList());
			
			// Conta arquivos que NÃO são OGG
			long nonOggFiles = allFiles.stream()
					.filter(path -> !path.toString().toLowerCase().endsWith(".ogg"))
					.count();
			
			lastAudioErrors = (int) nonOggFiles;
			
			if (nonOggFiles > 0) {
				System.err.println("[Phonk Edit Mod] Encontrados " + nonOggFiles + " arquivo(s) de áudio em formato inválido (só OGG é suportado!)");
				allFiles.stream()
						.filter(path -> !path.toString().toLowerCase().endsWith(".ogg"))
						.forEach(path -> System.err.println("[Phonk Edit Mod]   - " + path.getFileName() + " (formato inválido)"));
			}
			
		} catch (IOException e) {
			System.err.println("[Phonk Edit Mod] Erro ao verificar arquivos de áudio: " + e.getMessage());
		}
		
		return lastAudioErrors;
	}
	
	/**
	 * Retorna o caminho do diretório de imagens customizadas
	 */
	public static Path getCustomImagesDirectory() {
		return CUSTOM_IMAGES_DIR;
	}
}
